```mermaid
flowchart LR
%% Use Case Diagram: Online Shopping System
%%{ init: { 'theme': 'default' } }%%

graph TD
  Customer([<<actor>> Customer])
  Admin([<<actor>> Admin])

  UC_Browse["Browse Products"]
  UC_AddToCart["Add to Cart"]
  UC_Checkout["Checkout"]
  UC_TrackOrder["Track Order"]
  UC_Register["Register Account"]
  UC_Login["Login"]

  UC_ManageProducts["Manage Product Listings"]
  UC_ViewReports["View Sales Reports"]
  UC_HandleOrders["Handle Orders"]

  Customer --> UC_Browse
  Customer --> UC_AddToCart
  Customer --> UC_Checkout
  Customer --> UC_TrackOrder
  Customer --> UC_Register
  Customer --> UC_Login

  Admin --> UC_ManageProducts
  Admin --> UC_ViewReports
  Admin --> UC_HandleOrders```