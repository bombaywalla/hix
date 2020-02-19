(ns syntereen.hix.auth.views
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]
            [fork.core :as fork]
            [vlad.core :as v]
            [syntereen.hix.router :as router]
            [syntereen.hix.common.forms :as forms]
            [syntereen.hix.common.views :as views]
            )
  )



;; -- Login -------------------------------------------------------------------
;;

;; TODO: Should really be in a validation namespace
(def email-regex #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,63}$")
(defn is-email
  ([] (is-email {}))
  ([error-data] (v/predicate #(nil? (re-matches email-regex %))
                                (merge {:type :vlad.core/is-email} error-data))))
(defmethod v/english-translation :vlad.core/is-email
  [{:keys [name]}]
  (str name " must be a syntactically valid email address."))

(def login-form-validator-spec
  (v/join (v/attr ["email"]
                        (v/chain
                         (v/present)
                         (is-email)))
             (v/attr ["password"]
                        (v/chain
                         (v/present)))
             ))

(defn login-form-validator
  [values]
  (v/field-errors login-form-validator-spec values))


(defn login-form
  [login-denied]
  [fork/form {:path :login-form
              :form-id "login-form"
              :validation login-form-validator
              :prevent-default? true
              :clean-on-unmount? true
              :on-submit #(rf/dispatch [:login (:values %)])
              }
   (fn [{:keys [values
                form-id
                errors
                touched
                handle-change
                handle-blur
                submitting?
                handle-submit] :as props}]
     [:form {:id form-id :on-submit handle-submit}
      [:div
       [forms/email-input-view props {:name "email"
                                      :label "Email"
                                      :placeholder "user@example.com"
                                      :input-class (when login-denied "is-danger")}]
       (when (touched "email")
         [:p.help.is-danger (views/get-error-msg errors "email")])
       (when login-denied
         [:p.help.is-danger "Email or password is incorrect."])
       [forms/password-input-view props {:name "password"
                                         :label "Password"
                                         :placeholder "your password"
                                         :input-class (when login-denied "is-danger")}]
       (when (touched "password")
         [:p.help.is-danger (views/get-error-msg errors "password")])
       (when login-denied
         [:p.help.is-danger "Email or password is incorrect."])
       [:div]
       ]
      [:div.field.is-grouped.is-grouped-centered
       [:div.control
        [:button.button.is-primary.is-medium.is-fullwidth {:type "submit"} "Login"]]
       ]])])

(defn login
  []
  (let [errors @(rf/subscribe [:errors])
        login-errors (:login errors)
        login-denied (= 400 (:status login-errors))
        ;; TODO: handle 500, 0, -1 statuses as well
        ]
    [:div
     [:section.section
      [:div.container
       [:div.columns.is-centered
        [:div.column.is-one-third-desktop.is-half-tablet
         [:div.card
          [:div.card-content
           ;; (when login-errors (views/errors-list login-errors))
           (login-form login-denied)
           [:br]
           [:p.help "By logging in, you agree to the "
            [:a {:href "#"} "terms of service."]] ; TODO: Fix this URL
           [:p.has-text-centered
            [:a {:href (router/url-for :request-password-reset)} "Forgot your password?"]]
           [:p.has-text-centered
            [:a {:href (router/url-for :register)} "Don't have an account?"]]]]]]]]]))


;; -- Register ----------------------------------------------------------------
;;

(defn register-form-spec
  [passwd]
  (v/join
   (v/attr ["full-name"]
           (v/chain
            (v/present)))               ; TODO: need more
   (v/attr ["email"]
           (v/chain
            (v/present)
            (is-email)))
   (v/attr ["password"]
           (v/chain
            (v/present)
            (v/join
             (v/length-in 3 128)      ; TODO: Should be longer than 12
             (v/matches #"^.*[a-z].*$" {:message "Password must contain at least one lower-case letter."})
             (v/matches #"^.*[A-Z].*$" {:message "Password must contain at least one upper-case letter."})
             (v/matches #"^.*[0-9].*$" {:message "Password must contain at least one digit."})
             )))
   (v/attr ["confirm-password"]
           (v/chain
            (v/present)
            (v/join
             (v/length-in 3 128)     ; TODO: Should be longer than 12
             (v/matches #"^.*[a-z].*$" {:mesage "Password must contain at least one lower-case letter."})
             (v/matches #"^.*[A-Z].*$" {:mesage "Password must contain at least one upper-case letter."})
             (v/matches #"^.*[0-9].*$" {:message "Password must contain at least one digit."})
             (v/equals-value passwd {:message "Confirm password must match password."})
             )))
   (v/attr ["agree"]
           (v/equals-value true {:message "You must accept the terms of service."}))
   ))

;; v/present only seems to work on strings. It should really be non-nil or non-empty string.
;;
;; v/field-errors throws a NPE if one or more of the errors does not have a :selector field.
;; Since the v/equals-field is not associated with a field, the :selector is not there.

(defn register-form-validator
  [values]
  (v/field-errors (register-form-spec (get values "password")) values))

(defn register-form
  [email-taken invalid-password]
  [fork/form {:path :register-form
              :form-id "register-form"
              :validation register-form-validator
              :prevent-default? true
              :clean-on-unmount? true
              :on-submit #(rf/dispatch [:register-user (dissoc (dissoc (:values %)
                                                                       "confirm-password")
                                                               "agree")])
              }
   (fn [{:keys [values
                form-id
                errors
                touched
                handle-change
                handle-blur
                submitting?
                handle-submit] :as props}]
     [:form {:id form-id :on-submit handle-submit}
      [:div
       [forms/input-view props {:name "full-name"
                                 :label "Full name"
                                 :placeholder "Your full name"
                                 }]
       (when (touched "full-name")
         [:p.help.is-danger (views/get-error-msg errors "full-name")])

       [forms/email-input-view props {:name "email"
                                      :label "Email"
                                      :placeholder "user@example.com"
                                      :input-class (when email-taken "is-danger")}]
       [:p.help "We will email you a to verify the email address."]
       (when (touched "email")
         [:p.help.is-danger (views/get-error-msg errors "email")])
       (when email-taken
         [:p.help.is-danger "Email already taken."]) ;TODO: Ick. Wording needs to be better.

       [forms/password-input-view props {:name "password"
                                         :label "Password"
                                         :placeholder "Your password"
                                         :input-class (when invalid-password "is-danger")}]
       [:p.help "Please pick a strong password (12+chars, lower, upper, digit, special char)."]
       (when (touched "password")
         [:p.help.is-danger (views/get-error-msg errors "password")])
       (when invalid-password
         [:p.help.is-danger "Password is invalid (does not follow requirements)."])

       [forms/password-input-view props {:name "confirm-password"
                                         :label "Confirm password"
                                         :placeholder "Repeat your password"
                                         :input-class (when invalid-password "is-danger")}]
       (when (touched "confirm-password")
         [:p.help.is-danger (views/get-error-msg errors "confirm-password")])
       (when invalid-password
         [:p.help.is-danger "Password is invalid (does not follow requirements)."])
       [:div]
       ]
      [forms/checkbox-input-view props {:name "agree"
                                        :text (list " I agree to the "
                                                    [:a {:href "#"} "terms of service."])}] ; TODO: fix the href
      (when (touched "agree")
        [:p.help.is-danger (views/get-error-msg errors "agree")]) ; TODO: Fix vlad to allow customized, and then, i18n error messages.
      [:div.field.is-grouped.is-grouped-centered
       [:div.control
        [:button.button.is-primary.is-medium.is-fullwidth {:type "submit"} "Sign up"]]
       ]])])

(defn register
  []
  (let [errors @(rf/subscribe [:errors])
        register-errors (:register-user errors)
        client-error? (= 400 (:status register-errors))
        error-code (:error-code (:errors (:response register-errors)))
        email-taken (= error-code "user-exists")
        password-invalid (and client-error? (not email-taken)) ; TODO: hacky. improve.
        ;; TODO: handle 500, 0, -1 statuses as well
        ]
    [:div
     [:section.section
      [:div.container
       [:div.columns.is-centered
        [:div.column.is-half
         [:div.card
          [:div.card-content
           ;; (when register-errors (views/errors-list register-errors))
           (register-form email-taken password-invalid)
           [:br]
           [:p.has-text-centered
            [:a {:href (router/url-for :request-password-reset)} "Forgot your password?"]]
           [:p.has-text-centered
            [:a {:href (router/url-for :login)} "Already have an account?"]]]]]]]]]))

