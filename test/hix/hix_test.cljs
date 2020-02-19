(ns hix.hix-test
  (:require [re-frame.core :as rf]
            [day8.re-frame.test :as rftest]
            [cljs.test :refer-macros [deftest is]]
            [syntereen.hix.db :as db]
            [syntereen.hix.subs :as subs] ; to load in the subscriptions
            [syntereen.hix.events :as events]
            [syntereen.hix.auth.events :as auth]
            )
  )

(defn test-fixtures
  "Setup fixtures for hix tests."
  []
  (rf/reg-cofx
   :local-store-user
   (fn [cofx _]
     "Override :local-store-user cofx handler.
      Make sure the local store user is empty on start of tests."
     (assoc cofx :local-store-user
            (sorted-map)))))

(defn stub-login-response
  "Override the `:login` event-fx handler,
  to just dispatch the events the server would respond with."
  [expected-creds response-event]
  (rf/reg-event-fx
   :login
   (fn [_ [_ creds]]
     (is (= expected-creds creds))
     {:dispatch response-event})))

(deftest basic-sync-tests
  (rftest/run-test-sync
   (test-fixtures)
   (rf/dispatch [:initialize-db])

   ;; Define subscriptions to the app state
   (let [active-page (rf/subscribe [:active-page])
         loading (rf/subscribe [:loading])
         errors (rf/subscribe [:errors])
         user (rf/subscribe [:user])

         example-creds {:email "dc@brandx.net" :password "Thisis4Dorab."}
         example-user {:email "dc@brandx.net"
                       :full-name "Dorab Patel"
                       :token "somerandomstring"}
         ]

     ;; Assert the initial state
     (is (= :home @active-page))
     (is (nil? @loading))
     (is (empty? @errors))
     (is (empty? @user))

     ;; Simulate a successful login
     (stub-login-response example-creds
                          [:login-success {:user example-user}])
     (rf/dispatch [:login example-creds])

     ;; Assert a logged in state.
     (is (false? (:login @loading)))
     (is (= @user example-user))

     ;; Simulate an error login
     (stub-login-response example-creds
                          [:api-request-error :login {:status 400 :errors "Login failed"}])
     (rf/dispatch [:login example-creds])

     ;; Assert a failed login state
     (is (false? (:login @loading)))
     (is (= (:status (:login @errors)) 400))

     )))
