from persistence import *


def print_table(name: str, rows: list):
    print(name)
    for row in rows:
        print(f"({', '.join(map(repr, vars(row).values()))})\n")  # Ensures tuple formatting

        # print(f"{row}\n")


def print_employee_report():
    print("Employees report\n")
    query = """
        SELECT e.name, e.salary, b.location, 
               IFNULL(SUM(CASE WHEN a.quantity < 0 THEN -a.quantity * p.price ELSE 0 END), 0) AS total_sales_income
        FROM employees AS e
        LEFT JOIN branches AS b ON e.branche = b.id
        LEFT JOIN activities AS a ON e.id = a.activator_id
        LEFT JOIN products AS p ON a.product_id = p.id
        GROUP BY e.id
        ORDER BY e.name
    """
    rows = repo.execute_command(query)
    for row in rows:
        #  print(f"{row}\n")  

        print(' '.join(map(str, row)), end='\n\n') 


def print_activity_report():
    print("Activities report\n")
    query = """
        SELECT a.date, p.description, a.quantity,
               CASE WHEN a.quantity < 0 THEN e.name ELSE NULL END AS seller_name,
               CASE WHEN a.quantity > 0 THEN s.name ELSE NULL END AS supplier_name
        FROM activities AS a
        LEFT JOIN products AS p ON a.product_id = p.id
        LEFT JOIN employees AS e ON a.activator_id = e.id
        LEFT JOIN suppliers AS s ON a.activator_id = s.id
        ORDER BY a.date
    """
    rows = repo.execute_command(query)
    for row in rows:
        print(f"{row}\n")


def main():
    # Print tables
    print_table("\nActivities\n", repo.activities.find_all_Act())
    print_table("\nBranches\n", repo.branches.find_all())
    print_table("\nEmployees\n", repo.employees.find_all())
    print_table("\nProducts\n", repo.products.find_all())
    print_table("\nSuppliers\n", repo.suppliers.find_all())

    # Print reports
    print_employee_report()
    print_activity_report()


if __name__ == "__main__":
    main()
