(ns cheffy.auth0
  (:require [clj-http.client :as http]
            [muuntaja.core :as m]))

(defn get-test-token
  []
  (->> {:content-type :json
        :cookie-policy :standard
        :body         (m/encode "application/json"
                                {:client_id  "HuP0GLjDvFDGFS0lVnyC1JXf2nDyRoXg"
                                 :audience   "https://dev-p6wzd16k.us.auth0.com/api/v2/"
                                 :grant_type "password"
                                 :username   "testing@cheffy.app"
                                 :password   "Aman3c3r18$$"
                                 :scope      "openid profile email"})}
       (http/post "https://dev-p6wzd16k.us.auth0.com/oauth/token")
       (m/decode-response-body)
       :access_token))

(comment
  (get-test-token)


  ,)

