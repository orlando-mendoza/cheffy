(ns cheffy.recipe.handlers
  (:require
    [cheffy.recipe.db :as recipe-db]
    [ring.util.response :as response]
    [cheffy.responses :as responses])
  (:import (java.util UUID)))

(defn list-all-recipes
  [db]
  (fn [request]
    (let [uid "auth0|5ef440986e8fbb001355fd9c"
          recipes (recipe-db/find-all-recipes db uid)]
      (response/response recipes))))

(defn create-recipe!
  [db]
  (fn [request]
    (let [recipe-id (str (UUID/randomUUID))
          uid "auth0|5ef440986e8fbb001355fd9c"
          recipe (-> request :parameters :body)]
      (recipe-db/insert-recipe! db (assoc recipe :recipe-id recipe-id
                                                 :uid uid))
      (response/created (str responses/base-url "/recipes/" recipe-id)
                        {:recipe-id recipe-id}))))

(defn retrieve-recipe
  [db]
  (fn [request]
    (clojure.pprint/pprint request)
    (let [recipe-id (-> request :path-params :recipe-id)
          recipe (recipe-db/find-recipe-by-id db recipe-id)]
      (if recipe
        (response/response recipe)
        (response/not-found {:type    "recipe-not-found"
                             :message "Recipe not found"
                             :data    (str "recipe-id" recipe-id)})))))
