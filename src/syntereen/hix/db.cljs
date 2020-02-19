(ns syntereen.hix.db
  (:require [cljs.reader :as reader]
            [re-frame.core :as rf]
            )
  )

(def default-db
  {:active-page :home})

;;; ---------------- Local Storage ----------------

(def hix-user-key "hix-user")  ;; localstore key

(defn set-user-ls
  "Save the user in localStorage."
  [user]
  (.setItem js/localStorage hix-user-key (str user)))  ;; sorted-map written as an EDN map

(defn remove-user-ls
  "Remove the user from localStorage. Typically on logout."
  []
  (.removeItem js/localStorage hix-user-key))

(rf/reg-cofx
 :local-store-user
 (fn [cofx _]
   "Put the local-store user into the coeffect under :local-store-user.
   Read in user from localStore, and insert into a sorted map."
   (assoc cofx :local-store-user
          (into (sorted-map)
                (some->> (.getItem js/localStorage hix-user-key)
                         (reader/read-string))))))  ;; EDN map -> map
