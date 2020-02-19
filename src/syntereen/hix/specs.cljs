(ns syntereen.hix.specs
  (:require [cljs.spec.alpha :as s]
            [cljs.spec.gen.alpha :as gen]
            )
  )

(def non-empty-alphanum-string-gen
  "Generator for non-empty alphanumeric strings."
  (gen/such-that #(not= "" %)
                 (gen/string-alphanumeric)))

;; TODO: Can do better.
(def email-gen
  "Generator for email addresses."
  (gen/fmap
    (fn [[name host tld]]
      (str name "@" host "." tld))
    (gen/tuple
      non-empty-alphanum-string-gen
      non-empty-alphanum-string-gen
      non-empty-alphanum-string-gen)))

(def email-regex #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,63}$")
(s/def ::email-type (s/and string? #(re-matches email-regex %)))

(s/def ::keyword keyword?)
(s/def ::string string?)
(s/def ::non-empty-string
  (s/and ::string
         #(> (count %)0)))
(s/def ::boolean boolean?)
(s/def ::map map?)

(s/def ::active-page ::keyword)
(s/def ::full-name ::non-empty-string)
(s/def ::email
  (s/with-gen ::email-type
    (fn [] email-gen)))

(s/def ::loading? ::boolean)
(s/def ::errors ::map)

(s/def ::user
  (s/keys :req-un [::full-name ::email]))

(s/def ::db
  (s/keys :req-un [::active-page]
          :opt-un [::loading ::errors ::user]
          ))

