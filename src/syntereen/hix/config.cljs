(ns syntereen.hix.config
  "Hix configuration."
  (:require
   [aero.core :as aero]
   [cljs.tools.reader.reader-types :as rt]
   )
  )

;; NOTE: Not sure how to place this in a file
;; for a browser-based CLJS.
;; So, placed here.
(def hix-aero-config
  "{
   :api-url #profile {
                      :prod \"https://example.server.com:5555/api\"
                      :dev \"http://127.0.0.1:5554/api\"
                      :test \"http://127.0.0.1:5556/api\"
                      }
   :debug? #profile {
                     :prod false
                     :dev true
                     :test true
                     }
   }")

;; This is the default.
;; Overridden in shadow-cljs.edn
(goog-define ENV "prod")

(def hix-config (atom nil))

(defn init-config!
  "Initialize the Hix config."
  []
  (let [rdr (rt/source-logging-push-back-reader hix-aero-config)
        config (aero/read-config rdr {:profile (keyword ENV)})]
    (reset! hix-config config)))

(defn debug?
  []
  (:debug? @hix-config))

(defn api-url
  []
  (:api-url @hix-config))
