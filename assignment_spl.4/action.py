from persistence import *

import sys


def process_action(product_id: int, quantity: int, activator_id: int, date: str):
    product = repo.products.find(id=product_id)
    if not product:
        return  

    product = product[0]  

    if quantity > 0: 
        product.quantity += quantity
        stmt = "UPDATE products SET quantity = ? WHERE id = ?"
        repo._conn.execute(stmt, (product.quantity, product.id))

    elif quantity < 0:
        if product.quantity >= abs(quantity):  
            product.quantity += quantity  
            stmt = "UPDATE products SET quantity = ? WHERE id = ?"
            repo._conn.execute(stmt, (product.quantity, product.id))


    if quantity != 0: 
        activity = Activitie(product_id=product_id, quantity=quantity, activator_id=activator_id, date=date)
        repo.activities.insert(activity)
    



def main(args : list[str]):
    inputfilename : str = args[1]
    with open(inputfilename) as inputfile:
        for line in inputfile:
            splittedline : list[str] = line.strip().split(", ")
            #TODO: apply the action (and insert to the table) if possible
            splittedline = line.strip().split(", ")
            product_id = int(splittedline[0])
            quantity = int(splittedline[1])
            activator_id = int(splittedline[2])
            date = splittedline[3]

            # Process the action
            process_action(product_id, quantity, activator_id, date)

if __name__ == '__main__':
    main(sys.argv)