(ns syntereen.hix.common.forms
  (:require [fork.core :as fork]
            )
  )

(def input-view fork/input)

(defn email-input-view
  [{:keys [values touched handle-change handle-blur disabled?]}
   {:keys [label placeholder name type class input-class]}]
  [:div.field {:class class}
   [:label.label label]
   [:div.control.has-icons-left
    [:input.input {:name name
                   :class input-class
                   :placeholder placeholder
                   :type "email"
                   :value (values name "")
                   :disabled (disabled? name)
                   :on-change handle-change
                   :on-blur handle-blur}]
    [:span.icon.is-small.is-left [:i.fas.fa-envelope]]
    ]])

(defn password-input-view
  [{:keys [values touched handle-change handle-blur disabled?]}
   {:keys [label placeholder name type class input-class]}]
  [:div.field {:class class}
   [:label.label label]
   [:div.control.has-icons-left
    [:input.input {:name name
                   :class input-class
                   :placeholder placeholder
                   :type "password"
                   :value (values name "")
                   :disabled (disabled? name)
                   :on-change handle-change
                   :on-blur handle-blur}]
    [:span.icon.is-small.is-left [:i.fas.fa-lock]]
    ]])

(defn checkbox-input-view
  [{:keys [values touched handle-change handle-blur disabled?]}
   {:keys [name class text]}]           ; text can be either a string or a list of things
  [:div.field {:class class}
   [:div.control
    (into [:label.checkbox
           [:input
            {:name name
             :type "checkbox"
             :checked (values name false)
             :disabled (disabled? name)
             :on-change handle-change
             :on-blur handle-blur}]]
          (if (seq? text) text (list text)))]])

