(ns shalop.routes.home
  (:require [compojure.core :refer :all]
            [hiccup.core :refer :all]
            [hiccup.element :refer :all]
            [hiccup.page :refer :all]
            [shalop.views.layout :as layout]
            [hiccup.form :as form]
            [shalop.models.debe :as db]))

(declare home show-guests save-messages signup-content signup-error show-members)

(defroutes home-routes
           (GET "/" [] (home))
           (POST "/" [name message] (save-messages name message))
           (GET "/signup" [] (signup-content))
           (POST "/signup" [username password] (signup-error username password)))

(defn signup-content
  [& [username password error]]
  (html
    [:head
     [:link {:href "http://fonts.googleapis.com/css?family=Lato:300,400,400italic,600,700|Raleway:300,400,500,600,700|Crete+Round:400italic" :rel "stylesheet" :type "text/css"}]
     (include-css
       "/assets/css/bootstrap.css"
       "/assets/css/style.css"
       "/assets/css/dark.css"
       "/assets/css/font-icons.css"
       "/assets/css/animate.css"
       "/assets/css/magnific-popup.css"
       "/assets/css/responsive.css"
       "/assets/css/cr.css")
     (include-js "/assets/js/jquery.js"
                 "/assets/js/plugins.js")]

    [:section {:id "content"}
     [:div {:class "block divcenter col-padding signupbox"}
      [:div {:class "center marbott20"}
       [:img {:src "https://pbs.twimg.com/profile_images/633881274168438784/4NeZ9tad.png", :class "img-circle width110"}]]
      [:h2 {:class "uppercase ls1 center"} "Sign Up"]
      [:h4 error]
      (show-members)
      [:div {:class "row"}
       [:div {:class "col-sm-1"}]
       [:div {:class "col-sm-5"}
        [:form {:action "/signup" :post "/signup", :class "nobottommargin clearfix 300width"}
         [:div {:class "col_full marbott5"}
          [:label {:class "capitalize t600"} "Username:"]
          [:input {:type "username", :id "template-op-form-username", :name "template-op-form-username", :class "sm-form-control"} username]]
         [:div {:class "col_full marbott20"}
          [:label {:class "capitalize t600"} "Password:"]
          [:input {:type "password", :id "template-op-form-password", :name "template-op-form-password", :class "sm-form-control"} password]]
         [:div {:class "col_full nobottommargin"}
          [:div {:class "center martop30"}
           [:button {:type "submit", :class "button button-small button-border button-rounded", :value "submit"} "Submit"]]]]]
       [:div {:class "col-sm-1"}
        [:p {:class "nobottommargin title-center"}
         [:small {:class "t300"}
          [:em "or"]]]]
       [:div {:class "col-sm-2" :align "center"}
        [:div
         [:label {:class "capitalize t300"} "Sign Up With:"]
         [:br]]
        [:div
         [:a {:href "#", :class "social-icon si-borderless si-text-color si-facebook"}
          [:i {:class "icon-facebook"}]
          [:i {:class "icon-facebook"}]]
         [:a {:href "#", :class "social-icon si-borderless si-text-color si-twitter"}
          [:i {:class "icon-twitter"}]
          [:i {:class "icon-twitter"}]]
         [:a {:href "#", :class "social-icon si-borderless si-text-color si-google"}
          [:i {:class "icon-google"}]
          [:i {:class "icon-google"}]]]]]]]))

(defn signup-error
  [username password]
  (cond (empty? username)
        (signup-content username password "Enter your username properly")
        (empty? password)
        (signup-content username password "you forgot your password, dumb ass!!")
        :else
        (do
          (db/signup-db username password)
          (signup-content))))

(defn home [& [name message error]]
  (layout/common
    [:h1 "Biji kelapa gede palanya"]
    [:p "mahoru"]
    [:h4 error]
    (show-guests)
    [:hr]
    (form/form-to [:post "/"]
             [:p "Name:"]
             (form/text-field "name" name)
             [:p "Message:"]
             (form/text-area {:rows 10 :cols 40} "message" message)
             [:br]
             (form/submit-button "comment"))))

(defn save-messages [name message]
  (cond (empty? name)
        (home name message "Some dummy forgot to leave a name")
        (empty? message)
        (home name message "Don't you have something to say?")
        :else
        (do
          (db/save-message name message)
          (home))))

(defn format-time [timestamp]
  (-> "dd/MM/yyyy"
      (java.text.SimpleDateFormat.)
      (.format timestamp)))

(defn show-guests []
  [:ul.guests
   (for [{:keys [message name timestamp]}
         (db/read-guests)]
     [:li
      [:blockquote message]
      [:p "-" [:cite name]]
      [:time (format-time timestamp)]])])

(defn show-members []
  [:ul.guests
   (for [{:keys [username password]}
         (db/read-members)]
     [:li
      [:blockquote username]
      [:h6 password]])])