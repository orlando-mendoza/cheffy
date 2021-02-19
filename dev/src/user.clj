(ns user
  (:require
   [integrant.repl :as ig-repl]
   [integrant.core :as ig]
   [integrant.repl.state :as state]
   [cheffy.server]
   [next.jdbc :as jdbc]
   [next.jdbc.sql :as sql]))

(ig-repl/set-prep!
 (fn [] (-> "resources/config.edn" slurp ig/read-string)))

(def go ig-repl/go)
(def halt ig-repl/halt)
(def reset ig-repl/reset)
(def reset-all ig-repl/reset-all)

(def app (-> state/system :cheffy/app))
(def db (-> state/system :db/postgres))

#_(def router
    (reitit.core/router
     ["/v1/recipes/:recipe-id"
      {:coercion   reitit.coercion.spec/coercion
       :parameters {:path {:recipe-id int?}}}]
     {:compile reitit.coercion/compile-request-coercers}))

(comment
  (reitit.coercion/coerce!
   (reitit.core/match-by-path router "/v1/recipes/1234"))


  (app {:request-method :get
        :uri            "/swagger.json"})

  (-> {:request-method :get
       :uri            "/v1/recipes/1234-recipe"}
      app
      :body
      slurp)

  (-> {:request-method :get
       :uri            "/v1/recipes/341e178d-e450-4006-a478-b86fc7e040e8"}
      app
      :body
      slurp)

  (-> (app {:request-method :post
            :uri            "/v1/recipes"
            :body-params    {:name      "Lasagna"
                             :prep-time 59
                             :img       "image-url"}})
      :body
      (slurp))




  (require '[clojure.pprint :refer [pprint]]
           '[reitit.core]
           '[reitit.coercion]
           '[reitit.coercion.spec])

  (next.jdbc/execute! db ["SELECT * FROM recipe WHERE public = true"])
  (time (sql/find-by-keys db :recipe {:public true}))

  (time
   (with-open [conn (jdbc/get-connection db)]
     {:public (sql/find-by-keys conn :recipe {:public true})
      :drafts (sql/find-by-keys conn :recipe {:public false :uid "auth0|5ef440986e8fbb001355fd9c"})}))

  (with-open [conn (jdbc/get-connection db)]
    (let [recipe-id "a3dde84c-4a33-45aa-b0f3-4bf9ac997680"
          [recipe] (sql/find-by-keys conn :recipe {:recipe_id recipe-id})
          steps (sql/find-by-keys conn :step {:recipe_id recipe-id})
          ingredients (sql/find-by-keys conn :ingredient {:recipe_id recipe-id})]
      (when (seq recipe)
        (assoc recipe
               :recipe/steps steps
               :recipe/ingredients ingredients))))

  (go)
  (halt)
  (reset)

  (set! *print-namespace-maps* false)

  ,)
