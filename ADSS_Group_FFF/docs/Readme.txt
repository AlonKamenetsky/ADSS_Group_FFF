Alon 213795107
Eran 207778788
Stav 207458571
Daniel 336182621

To use the Inventory and Suppliers Management System, run the program from the generated .jar file.
Upon launch, a main menu will appear, allowing the user to choose between the Suppliers Module and the Inventory Module.
Before performing any operations, it is recommended to load sample data from CSV files, which will delete all existing data and populate the database with predefined suppliers,
products, contracts, and orders.
 The system is entirely menu-driven: users interact with it by entering numbers to navigate between modules and actions. The Suppliers Module offers comprehensive functionality:


Add a product → from Suppliers Module, enter 1, then 1.
Console:
Enter product name: → Milk
Enter product company: → Tnuva
Enter product category (0-6): → 0

Update a product → enter 1, then 2.
Console:
Which product you want to change? Enter product: → 1
Enter new product name: → Skimmed Milk
Enter new product company: → Tara

Delete a product → enter 1, then 3.
Console:
Which product do you want to delete? Enter product ID: → 1
Print a specific product → enter 1, then 4.
Console:
Which product do you want to search? Enter product ID: → 2

Print all products → enter 1, then 5.
Console:
(Displays all registered products with their IDs and details)

Register a new supplier → enter 2, then 1.
Console:
Enter supplier name: → Tnuva
Enter product category (0-6): → 0
Enter bank account ID: → 123456
Enter payment method: → 1
Enter delivery method: → 1
(Then prompts for phone, address, email, contact name, supply method, supply days, etc.)

Update supplier name → enter 2, then 2, then 1.
Console:
Enter supplier ID: → 1
Enter new supplier name: → New Tnuva

Update supplier delivery method → enter 2, then 2, then 2.
Console:
Enter supplier ID: → 1
Enter delivery method: → 0

Update supplier contact info → enter 2, then 2, then 3.
Console:
Enter new phone number: → 0501234567
Enter address: → Tel Aviv
Enter email: → supplier@email.com
Enter contact name: → David Cohen

Update supplier payment method → enter 2, then 2, then 4.
Console:
Enter new bank account ID: → 654321
Enter payment method: → 0

Delete a supplier → enter 2, then 3.
Console:
Enter supplier ID: → 1

Print a specific supplier → enter 2, then 4.
Console:
Enter supplier ID: → 1

Print all suppliers → enter 2, then 5.
Console:
(Displays all registered suppliers)

Register a new contract → enter 3, then 1.
Console:
Enter supplier ID: → 1
Enter product ID: → 2
Enter product price: → 12
Enter quantity for discount: → 10
Enter discount percentage: → 15
(Repeat for more products, or enter -1 to finish)

Delete a contract → enter 3, then 2.
(Function stub – to be implemented)
Print a specific contract → enter 3, then 3.
Console:
Enter contract ID: → 1001

Print all contracts → enter 3, then 4.
Console:
(Displays all contracts in the system)

Create a new order → enter 4, then 1.
Console:
Enter product ID: → 1
Enter quantity: → 50
Enter delivery date (Enter T for tomorrow): → T

Create a scheduled order → enter 4, then 2.
Console:
Enter product ID: → 3
Enter quantity: → 30
Enter regular day to be ordered: → 3

Update order contact info → enter 4, then 3, then 1.
Console:
Enter order ID: → 5
Enter new phone number: → 0501112222
Enter address: → Haifa
Enter email: → client@mail.com
Enter contact name: → Dana

Update order supply date → enter 4, then 3, then 2.
Console:
Enter order ID: → 5
Enter new supply date: → 10/06/2025

Remove products from an order → enter 4, then 3, then 3.
Console:
Enter product ID to remove (enter -1 to finish): → 1

Add products to an order → enter 4, then 3, then 4.
Console:
(Displays available contract options)
Enter product ID: → 2
Enter quantity: → 40

Update order status → enter 4, then 3, then 5.
Console:
Enter order status: → 2 (DELIVERED)

Delete an order → enter 4, then 4.
Console:
Enter order ID: → 5

Print a specific order → enter 4, then 5.
Console:
Enter order ID: → 5

Print all orders → enter 4, then 6.
Console:
(Displays all orders with status and contents)

Print all scheduled orders → enter 4, then 7.
Console:
(Displays all recurring scheduled orders)
The Inventory Module allows users to manage and update product quantities, change categories, and generate low-stock reports.
Together, the modules support both manual and automatic order handling, using contract data to automatically select the cheapest supplier for each product,
providing a complete and interactive logistics system.


The Inventory CLI is a simple text-based interface for managing products, categories, and reports in an inventory system. It relies on an underlying InventoryService that handles business logic, and exposes the following core features:

View all products in inventory

View only low-stock items (products whose quantities have fallen below their minimum thresholds)

View all categories (including parent/child relationships)

Update shelf or backroom quantities for a specific item

Mark an item as damaged

Generate a one-time inventory report with optional filters (by category and/or status)

Add a new product (by selecting from a supplier’s catalog, then supplying purchase/sale price and initial quantities)

Add a new category (with optional parent)

Exit the CLI



Sample Interaction
Below is a sample session (user input is highlighted in bold):

markdown
Copy
Edit
=== Welcome to Inventory CLI ===

Choose an option:
1. View Inventory
2. View Low Stock
3. View Categories
4. Update Quantities
5. Mark Damaged
6. Generate Report
7. Add New Product
8. Add New Category
9. Exit
Choice: **3**

(No categories defined yet.)

Choose an option:
1. View Inventory
2. View Low Stock
3. View Categories
4. Update Quantities
5. Mark Damaged
6. Generate Report
7. Add New Product
8. Add New Category
9. Exit
Choice: **8**

Category name: **Electronics**
Enter parent category name (or leave blank): **

Category added.

Choose an option:
1. View Inventory
2. View Low Stock
3. View Categories
4. Update Quantities
5. Mark Damaged
6. Generate Report
7. Add New Product
8. Add New Category
9. Exit
Choice: **3**

Name                 | Parent
----------------------+----------------------
Electronics          | None

Choose an option:
1. View Inventory
2. View Low Stock
3. View Categories
4. Update Quantities
5. Mark Damaged
6. Generate Report
7. Add New Product
8. Add New Category
9. Exit
Choice: **7**

Available supplier products:
ID    Name                 Manufacturer
----- -------------------- ---------------
101   Widget A             Acme Corp
102   Gizmo B              Gadget Inc

Enter the ID of the product you want to add: **101**
Enter purchase price for this product: **12.50**
Enter sale price for this product: **19.99**
Initial shelf quantity (integer): **10**
Initial backroom quantity (integer): **20**
Minimum threshold (integer): **5**

Product added to inventory.

Choose an option:
1. View Inventory
2. View Low Stock
3. View Categories
4. Update Quantities
5. Mark Damaged
6. Generate Report
7. Add New Product
8. Add New Category
9. Exit
Choice: **1**

ID   | Name      | Manufacturer | Shelf | Backroom | Min | Purchase | Sale  | Status  | Category
--------------------------------------------------------------------------------------------
101  | Widget A  | Acme Corp    | 10    | 20       |  5  |   12.50  | 19.99 | NORMAL  | Electronics

Choose an option:
…  
Choice: **9**

Logging out Inventory, returning to main menu





