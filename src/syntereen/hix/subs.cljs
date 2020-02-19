(ns syntereen.hix.subs
  (:require [re-frame.core :as rf]
            )
  )

(rf/reg-sub
 :active-page
 (fn [db _]
   (:active-page db)))

(rf/reg-sub
 :loading
 (fn [db _]
   (:loading db)))

(rf/reg-sub
 :errors
 (fn [db _]
   (:errors db)))

(rf/reg-sub
 :user
 (fn [db _]
   (:user db)))
