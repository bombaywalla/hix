(ns syntereen.hix.config
  "Hix configuration."
  (:require
   [aero.core :as aero]
   [cljs.tools.reader.reader-types :as rt]
   [shadow.resource :as sr]
   )
  )

;; Reads file into var at compile time
;; File relative to this ns.
(def hix-aero-config
  (sr/inline "./hix.edn"))

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
