(ns syntereen.hix.router
  (:require [bidi.bidi :as bidi]
            [pushy.core :as pushy]
            [re-frame.core :as rf]))

;;; See: https://pupeno.com/2015/08/26/no-hashes-bidirectional-routing-in-re-frame-with-bidi-and-pushy/
;;; by J. Pablo Fernández

;;; pushy gives us nice looking urls. Normally the URLs would include a #
;;; Using pushy we can use '/about' instead of '/#/about'.
;;; pushy takes three arguments:
;;; dispatch-fn - dispatches when a match is found
;;; match-fn - checks if a route exists
;;; identity-fn (optional) - extracts the route from value returned by match-fn

(def routes
  ["/" {""         :home
        "login"    :login
        "logout"   :logout
        "register" :register
  }])

(def history
  (let [dispatch #(rf/dispatch 
                   [:set-active-page {:page      (:handler %)
                                      :profile   (get-in % [:route-params :user-id])}])
        match #(bidi/match-route routes %)]
    (pushy/pushy dispatch match)))

(defn start!
  "Called at startup. Sets the event handlers for pushy."
  []
  (pushy/start! history))

(defn url-for
  "Convert a `route` to a URL."
  [route]
  (bidi/path-for routes route))

(defn set-token!
  "Used to set the URL and the history manually.
  Calls the pushy dispatch function."
  [path]
  (pushy/set-token! history path))
