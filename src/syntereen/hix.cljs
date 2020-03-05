(ns syntereen.hix
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            [syntereen.hix.config :as config]
            [syntereen.hix.router :as router]

            ;; to make the compiler load these
            [syntereen.hix.events]
            [syntereen.hix.subs]
            [syntereen.hix.db]
            [syntereen.hix.auth.events]

            [syntereen.hix.common.views :as common]
            [syntereen.hix.auth.views :as auth]
            [syntereen.hix.views :as views]
            )
  )



(def pages
  {
   :home     views/home
   :login    auth/login
   :register auth/register
   })

(defn hix-app
  []
  (let [active-page @(rf/subscribe [:active-page])
        page (get pages active-page common/not-found-view)
        ]
    [:div
     [common/header]
     [page]
     [common/footer]]))

(defn dev-setup []
  (when (config/debug?)
    (enable-console-print!)
    (println "Running in dev mode")))

(defn mount-root []
  (rf/clear-subscription-cache!)
  (r/render [hix-app]
            (.getElementById js/document "app")))

(defn ^:export init []
  (config/init-config!)
  (dev-setup)
  (router/start!)
  (rf/dispatch-sync [:initialize-db])
  (mount-root))
