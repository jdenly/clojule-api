(ns clojule-api.service-test
  (:require [clojure.test :refer :all]
            [io.pedestal.test :refer :all]
            [io.pedestal.http :as bootstrap]
            [clojule-api.service :as service]
            [clojure.data.json :as json]))

(def service
  (::bootstrap/service-fn (bootstrap/create-servlet service/service)))

(deftest roll-dice-test
  (is (=
        (count (json/read-str (:body (response-for service :get "/roll-dice?n=5"))))
        5))
  (is (=
        (count (json/read-str (:body (response-for service :get "/roll-dice?n=3"))))
        3)))
