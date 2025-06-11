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