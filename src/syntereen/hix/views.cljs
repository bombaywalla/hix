(ns syntereen.hix.views
  (:require [clojure.string :as str]
            [re-frame.core :as rf]
            )
  )

;;; TODO: Incorporate i18n throughout.

;;; TODO: Need to have an i18n function.
(defn familiar-name
  [full-name]
  (when full-name
    (if-let [first-blank (str/index-of full-name " ")] ; TODO: i18n?
      (subs full-name 0 first-blank)
      full-name)))



;; -- Home --------------------------------------------------------------------
;;

(defn home
  "The home page."
  []
  (let [user     @(rf/subscribe [:user])
        full-name (:full-name user)
        user-name (familiar-name full-name)
        ]
    [:section.section
     [:div.container
      [:h1.title "HIX"]
      [:h2.subtitle (if user-name
                      (str "User \"" user-name "\" is logged into the service.")
                      "A frontend to an API.")
       ]]]))

