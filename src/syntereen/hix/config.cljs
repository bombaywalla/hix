(ns syntereen.hix.config)

(goog-define ENV "prod")

(def hix-config
  {"prod" {
          :debug? false
          :api-url "https://example.server.com:5555/api"
          }
   "dev" {
          :debug? true
          :api-url "http://127.0.0.1:5554/api"
         }
   "test" {
          :debug? true
          :api-url "http://127.0.0.1:5556/api"
          }
   })

(defn debug?
  []
  (:debug? (get hix-config ENV)))

(defn api-url
  []
  (:api-url (get hix-config ENV)))
