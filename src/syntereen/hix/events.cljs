(ns syntereen.hix.events
  (:require
   [re-frame.core :as rf]
   [day8.re-frame.http-fx] ; This is not used explicitly, but is needed so :http-xhrio can self-register
   [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]
   [ajax.core :as ajax]
   [syntereen.hix.config :as config]
   [syntereen.hix.router :as router]
   [syntereen.hix.db :as db]
   [clojure.string :as str]
   )
  )

;;; This interceptor chain is used by all event handlers which manipulate user.
(def set-user-interceptor
  [(rf/path :user)        ;; `:user` path within `db`, rather than the full `db`.
   (rf/after db/set-user-ls) ;; write user to localstore (after)
   rf/trim-v])            ;; removes first (event id) element from the event vec

;;; After logging out, remove the user from localStorage.
(def remove-user-interceptor
  [(rf/after db/remove-user-ls)])


(defn endpoint
  "Concat any params to api-url separated by /"
  [& params]
  (str/join "/" (concat [(config/api-url)] params)))

(rf/reg-fx
 :set-url
 (fn [{:keys [url]}]
   (router/set-token! url)))

;; -- :initialize-db ----------------------------------------------------------
;;
(defn-traced initialize-db-handler
  [{:keys [local-store-user]} _]
  {:db (assoc db/default-db :user local-store-user)})

;;; gets user from localstore, and puts into coeffects arg
(rf/reg-event-fx
 :initialize-db
 [(rf/inject-cofx :local-store-user)]
 initialize-db-handler)

(defn-traced set-active-page-handler
  [{:keys [db]} [_ {:keys [page user-id]}]]
  (let [new-db (assoc db :active-page page)]
    {:db new-db}))

;;; triggered when the user clicks on a link that redirects to a another page
(rf/reg-event-fx
 :set-active-page
 set-active-page-handler)

;; -- Logout ------------------------------------------------------------------
;;

;;; The event handler function removes the user from
;;; app-state and sets the url to "/".
(defn-traced logout-handler
  [{:keys [db]} _]
  {:db (dissoc (dissoc db :errors) :user)
   :dispatch [:set-active-page {:page :home}]})

(rf/reg-event-fx
 :logout
 remove-user-interceptor
 logout-handler)

;; -- Server API Request Handlers ----------------
;;

(defn-traced api-request-success-handler
  [db [_ request-type]]
  (assoc-in (dissoc db :errors)
            [:loading request-type] false))

(rf/reg-event-db
 :api-request-success
 api-request-success-handler)

(defn-traced api-request-error-handler
  [db [_ request-type response]]
  (-> db
      (assoc-in [:errors request-type] response)
      (assoc-in [:loading request-type] false)))

(rf/reg-event-db
 :api-request-error
 api-request-error-handler)
