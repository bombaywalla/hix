(ns syntereen.hix.auth.events
  (:require [re-frame.core :as rf]
            [day8.re-frame.http-fx] ; This is not used explicitly, but is needed so :http-xhrio can self-register
            [day8.re-frame.tracing :refer-macros [defn-traced]]
            [ajax.core :as ajax]
            [fork.core :as fork]
            [syntereen.hix.events :as events]
            )
  )

;; -- POST Login @ /api/users/login -------------------------------------------
;;
(defn-traced login-handler
  "Given `credentials` = {`:email` ... `:password` ...}, send API request to login the user."
  [{:keys [db]} [_ credentials]]
  {:db         (fork/set-submitting (assoc-in db [:loading :login] true) :login-form false)
   :http-xhrio {:method          :post
                :uri             (events/endpoint "users" "login")
                :params          {:user credentials}
                :format          (ajax/json-request-format)
                :response-format (ajax/json-response-format {:keywords? true})
                :on-success      [:login-success]
                :on-failure      [:api-request-error :login]}})

(rf/reg-event-fx
 :login
 ;; :login-form can be anything, but must match that in login-handler
 [(fork/on-submit :login-form)]
 login-handler)

(defn-traced login-success-handler
  [{user :db} [{props :user}]]
  {:db (merge user props)
   :dispatch-n [[:api-request-success :login]
                [:set-active-page {:page :home}]]})

(rf/reg-event-fx
 :login-success
 events/set-user-interceptor
 login-success-handler)

;; -- POST Registration @ /api/users ------------------------------------------
;;

(defn-traced register-user-handler
  "Given `registration` = {`:full-name` ... `:email` ... `:password` ...}, send A{I request to register the user."
  [{:keys [db]} [_ registration]]
  {:db         (fork/set-submitting (assoc-in db [:loading :register-user] true) :register-form false)
   :http-xhrio {:method          :post
                :uri             (events/endpoint "users")
                :params          {:user registration}
                :format          (ajax/json-request-format)
                :response-format (ajax/json-response-format {:keywords? true})
                :on-success      [:register-user-success]
                :on-failure      [:api-request-error :register-user]}})

(rf/reg-event-fx
 :register-user
 ;; :register-form can be anything, but must match that in register-user-handler
 [(fork/on-submit :register-form)]
 register-user-handler)

(defn-traced register-user-success-handler
  [{user :db} [{props :user}]]
  {:db (merge user props)
   :dispatch-n [[:api-request-success :register-user]
                [:set-active-page {:page :home}]]})

(rf/reg-event-fx
 :register-user-success
 events/set-user-interceptor
 register-user-success-handler)
