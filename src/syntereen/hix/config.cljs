(ns syntereen.hix.config)

(goog-define ENV "prod")

(def hix-config
  {"prod" {
          :debug? false
          :api-url "https://example.server.com/api"
          }
   "dev" {
          :debug? true
          :api-url "http://localhost:5555/api"
         }
   "test" {
          :debug? true
          :api-url "http://localhost:5555/api"
          }
   })

(defn debug?
  []
  (:debug? (get hix-config ENV)))

(defn api-url
  []
  (:api-url (get hix-config ENV)))
