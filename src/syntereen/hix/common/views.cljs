(ns syntereen.hix.common.views
  (:require [re-frame.core :as rf]
            [reagent.core :as reagent]
            [syntereen.hix.router :as router]
            [syntereen.hix.views :as views]
            )
  )

(defn errors-list
  "Print out the errors map. Mostly during debugging."
  [errors]
  [:div.field
   [:label.label "Errors"]
   [:ul.help.is-danger
    (for [[key val] errors]
      ^{:key key} [:li (str (name key) " " val)])
    ]])

(defn get-error-msg
  "Obtain the error message for the field `name`from the `errors`."
  [errors name]
  (first (get errors (list name))))

(defn section
  "Create a section and populate it with the `views`."
  [& views]
  [:section.section
   (into [:div.container] views)])

(defn sized-section
  "Create a section of the given `size` (e.g. :is-half) and populate it with the `views`."
  [size & views]
  [:section.section
   [:div.container
    [:div.columns.is-centered
     (into [:div.column {:class (name size)}] views)]]])

(defn not-found-view
  "View for a page not found."
  []
  [section
   [:div.has-text-centered
    [:div.subtitle "The page you are looking for was not found."]
    [:div.subtitle [:a {:href "/"} "Go to the home page."]]]])

(defn header
  "The header for all pages."
  []
  (let [*menu-active? (reagent/atom false)
        toggle-menu-fn #(swap! *menu-active? not)
        ]
    (fn []
      (let [menu-active? @*menu-active?
            user        @(rf/subscribe [:user])
            full-name (:full-name user)
            active-page @(rf/subscribe [:active-page])
            wap (fn [pg it] (when (= active-page pg) it))
            logout-user (fn [ev] (.preventDefault ev) (rf/dispatch [:logout]))
            ]
        [:nav.navbar.is-fixed-top.is-primary {:role "navigation" :aria-label "main navigation"}
         [:div.container
          [:div.navbar-brand
           [:a.navbar-item (cond-> {:href (router/url-for :home)}
                             menu-active? (assoc :on-click toggle-menu-fn))
            "HIX"]
           [:a.navbar-burger.burger (cond-> {:role "button"
                                             :aria-label "menu"
                                             :aria-expanded menu-active?
                                             :data-target "navbarMenu"
                                             :on-click toggle-menu-fn}
                                      menu-active? (assoc :class "is-active"))
            [:span {:aria-hidden "true"}]
            [:span {:aria-hidden "true"}]
            [:span {:aria-hidden "true"}]]]
          [:div#navbarMenu.navbar-menu (when menu-active? {:class "is-active"})
           [:div.navbar-start
            [:a.navbar-item (cond-> {:href (router/url-for :home)}
                              menu-active? (assoc :on-click toggle-menu-fn))
             "Home"]
            ]
           [:div.navbar-end
            [:div.navbar-divider]
            (if (empty? user)
              ;; if user
              [:a.navbar-item
               (cond-> {:href (router/url-for :login)}
                 menu-active? (assoc :on-click toggle-menu-fn))
               [:strong "Sign in"]]
              ;; if not user
              [:a.navbar-item
               (cond-> {:href (router/url-for :logout)
                        :on-click logout-user}
                 menu-active? (assoc :on-click (fn [ev] (logout-user ev) (toggle-menu-fn))))
               [:strong (str "Sign out " (views/familiar-name full-name))]
               ])
            (when (empty? user)
              [:a.navbar-item
               (cond-> {:href (router/url-for :register)}
                 menu-active? (assoc :on-click toggle-menu-fn))
               "Sign up"])
            ]]]
         ]))))



(defn footer
  "The footer for all pages."
  []
  [:footer.footer
   [:div.content.has-text-centered
    [:p [:strong "HIX" ] " MIT License."]]])


(defn pricing-plans
  ;; TODO: actually make it programmable
  []
  [:div.pricing-table

   [:div.pricing-plan.is-active
    [:div.plan-header "Free"]
    [:div.plan-price [:span.plan-price-amount [:span.plan-price-currency "$"] "0"] "/month"]
    [:div.plan-items
     [:div.plan-item "1 Channel"]
     [:div.plan-item "1 Channel account"]
     [:div.plan-item "1 Channel campaign"]
     ]
    [:div.plan-footer [:button.button.is-fullwidth {:disabled "disabled"} "Current plan"]]
    ]

   [:div.pricing-plan.is-warning
    [:div.plan-header "Starter"]
    [:div.plan-price [:span.plan-price-amount [:span.plan-price-currency "$"] "200"] "/month"]
    [:div.plan-items
     [:div.plan-item "1 Channel"]
     [:div.plan-item "1 Channel account"]
     [:div.plan-item "10 Channel campaigns"]
     ]
    [:div.plan-footer [:button.button.is-fullwidth "Choose"]]
    ]

   [:div.pricing-plan.is-success
    [:div.plan-header "Growing agency"]
    [:div.plan-price [:span.plan-price-amount [:span.plan-price-currency "$"] "2000"] "/month"]
    [:div.plan-items
     [:div.plan-item "2 Channels"]
     [:div.plan-item "10 Channel accounts"]
     [:div.plan-item "100 Channel campaigns"]
     ]
    [:div.plan-footer [:button.button.is-fullwidth "Choose"]]
    ]

   [:div.pricing-plan.is-danger
    [:div.plan-header "Enterprise"]
    [:div.plan-price [:span.plan-price-amount [:span.plan-price-currency "$"] "20000"] "/month"]
    [:div.plan-items
     [:div.plan-item "10 Channels"]
     [:div.plan-item "100 Channel accounts"]
     [:div.plan-item "10,000 Channel campaigns"]
     ]
    [:div.plan-footer [:button.button.is-fullwidth "Choose"]]
    ]
   ])
