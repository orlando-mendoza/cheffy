(ns cheffy.recipes-tests
  (:require [clojure.test :refer :all]
            [cheffy.server :refer :all]
            [cheffy.test-system :as ts]))

(use-fixtures :once ts/token-fixture)

(def recipe-id (atom nil))

(def recipe
  {:img       "http://thisIsMyImage.com"
   :prep-time 30
   :name      "My test recipe"})

(def update-recipe
  (assoc recipe :public true))

(deftest recipes-tests
  (testing "List recipes"
    (testing "with auth -- public and drafts"
      (let [{:keys [status body]} (ts/test-endpoint :get "/v1/recipes" {:auth true})]
        (is (= 200 status))
        (is (vector? (:public body)))
        (is (vector? (:drafts body))))))

  (testing "without auth -- public"
    (let [{:keys [status body]} (ts/test-endpoint :get "/v1/recipes" {:auth false})]
      (is (= 200 status))
      (is (vector? (:public body)))
      (is (nil? (:drafts body))))))

(deftest recipe-tests
  (testing "Create recipe"
    (let [{:keys [status body]} (ts/test-endpoint :post "/v1/recipes" {:auth true :body recipe})]
      (reset! recipe-id (:recipe-id body))
      (is (= status 201))))


  (testing "Update recipe"
    (let [{:keys [status]} (ts/test-endpoint :put (str "/v1/recipes/" @recipe-id) {:auth true :body update-recipe})]
      (is (= status 204))))

  (testing "Favorite recipe"
    (let [{:keys [status]} (ts/test-endpoint :post (str "/v1/recipes/" @recipe-id "/favorite")
                                             {:auth true})]
      (is (= status 204))))

  (testing "Unfavorite recipe"
    (let [{:keys [status]} (ts/test-endpoint :delete (str "/v1/recipes/" @recipe-id "/favorite")
                                             {:auth true})]
      (is (= status 204))))

  (testing "Delete recipe"
    (let [{:keys [status]} (ts/test-endpoint :delete (str "/v1/recipes/" @recipe-id) {:auth true})]
      (is (= status 204)))))

(comment
  (reset! recipe-id (-> (ts/test-endpoint :post
                                          "/v1/recipes"
                                          {:auth true :body recipe})
                        :body
                        :recipe-id))

  (ts/test-endpoint :put
                    (str "/v1/recipes/" @recipe-id)
                    {:auth true :body update-recipe})

  (ts/test-endpoint :delete
                    (str "/v1/recipes/" @recipe-id)
                    {:auth true})

  ; Test favorite recipe
  (ts/test-endpoint :post
                    "/v1/recipes/b10b3240-9631-4b48-a409-b0b5c8d63011/favorite"
                    {:auth true})
  (ts/test-endpoint :delete
                    "/v1/recipes/b10b3240-9631-4b48-a409-b0b5c8d63011/favorite"
                    {:auth true}),)
