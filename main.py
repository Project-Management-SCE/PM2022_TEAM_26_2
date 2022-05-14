import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
from firebase_admin import auth

cred = credentials.Certificate('firebase-sdk.json')

firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://ymd-420-default-rtdb.firebaseio.com/'
})


email = input("Please enter your email adders: ")
password = input("Please enter your password: ")

# TODO-> add checking if input pass equals to user's saved pass
user = auth.get_user_by_email(email)
if user is not None:
    Tables = ['Users', 'Accounts', 'ClerkCustomers', 'Loans']
    for table in Tables:
        ref = db.reference(table)
        if ref.get() is None:
            ref.push(table)
        # print(table + '\n')
        # print(ref.get())
        # print('\n')
