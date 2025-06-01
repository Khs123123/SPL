import sqlite3
import atexit
from dbtools import Dao
 
# Data Transfer Objects:
class Employee(object):
    #TODO: implement
    def __init__(self, id, name, salary, branche):
        self.id = int(id)
        self.name = name
        self.salary = float(salary)
        self.branche = int(branche)

    def __str__(self):
        return f"{self.id}, {self.name}, {self.salary}, {self.branche}"
    pass
 
class Supplier(object):
    #TODO: implement
    def __init__(self, id, name, contact_information):
        self.id = int(id)
        self.name = name
        self.contact_information = contact_information

    def __str__(self):
        return f"{self.id}, {self.name}, {self.contact_information}"
    pass

class Product(object):
    #TODO: implement
    def __init__(self, id, description, price, quantity):
        self.id = int(id)
        self.description = description
        self.price = float(price)
        self.quantity = int(quantity)

    def __str__(self):
        return f"{self.id}, {self.description}, {self.price}, {self.quantity}"
    pass

class Branche(object):
    #TODO: implement
    def __init__(self, id, location, number_of_employees):
        self.id = int(id)
        self.location = location
        self.number_of_employees = int(number_of_employees)

    def __str__(self):
        return f"{self.id}, {self.location}, {self.number_of_employees}"
    pass

class Activitie(object):
    #TODO: implement
    def __init__(self, product_id, quantity, activator_id, date):
        self.product_id = int(product_id)
        self.quantity = int(quantity)
        self.activator_id = int(activator_id)
        self.date = date

    def __str__(self):
        return f"{self.product_id}, {self.quantity}, {self.activator_id}, {self.date}"
    pass
 
 
#Repository
class Repository(object):
    def __init__(self):
        self._conn = sqlite3.connect('bgumart.db')
        #TODO: complete
        self.employees = Dao(Employee, self._conn)
        self.suppliers = Dao(Supplier, self._conn)
        self.products = Dao(Product, self._conn)
        self.branches = Dao(Branche, self._conn)
        self.activities = Dao(Activitie, self._conn)
 
    def _close(self):
        self._conn.commit()
        self._conn.close()
 
    def create_tables(self):
        self._conn.executescript("""
            CREATE TABLE employees (
                id              INT         PRIMARY KEY,
                name            TEXT        NOT NULL,
                salary          REAL        NOT NULL,
                branche    INT REFERENCES branches(id)
            );
    
            CREATE TABLE suppliers (
                id                   INTEGER    PRIMARY KEY,
                name                 TEXT       NOT NULL,
                contact_information  TEXT
            );

            CREATE TABLE products (
                id          INTEGER PRIMARY KEY,
                description TEXT    NOT NULL,
                price       REAL NOT NULL,
                quantity    INTEGER NOT NULL
            );

            CREATE TABLE branches (
                id                  INTEGER     PRIMARY KEY,
                location            TEXT        NOT NULL,
                number_of_employees INTEGER
            );
    
            CREATE TABLE activities (
                product_id      INTEGER REFERENCES products(id),
                quantity        INTEGER NOT NULL,
                activator_id    INTEGER NOT NULL,
                date            TEXT    NOT NULL
            );
        """)

    def execute_command(self, script: str) -> list:
        return self._conn.cursor().execute(script).fetchall()
 
# singleton
repo = Repository()
atexit.register(repo._close)