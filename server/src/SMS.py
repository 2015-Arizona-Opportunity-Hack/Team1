from __init__ import app, db
from flask import Flask, request, redirect
from twilio.rest import TwilioRestClient
import twilio.twiml
from db_layer import Update, User
from werkzeug.security import generate_password_hash

ACCOUNT_SID = "AC67473fa50b6b5f01e75e48a9389b882a"  # ADD ACCOUNT
token = "25336919c955c11bec22e4185683f662"  # ADD TOKEN
sender = "+15204474279"  # ADD SENDER
client = TwilioRestClient(ACCOUNT_SID, token)

"""
@app.route("/ChangePhoneNumber", methods=['GET', 'POST'])
def change_of_phone():
    phone = request.values.get('From', None)
    body = request.values.get('Body', None)
    model = db.find_by_field("phone_number", phone, Update)

    if model:
        user = db.find_by_field("username", model.username, User)  # the body is probably a password

        hashed_body = generate_password_hash(body, "pbkdf2:sha256:10000")
        if hashed_body == user.password_hash:
            user.phone_number = phone
            db.update(user)
            message = "Successfully Updated Phone Number"
        else:
            message = "Phone Failed to be Updated due to conflicting password"
    else:
        user = db.find_by_field("phone_number", phone, User)
        db.insert(Update(phone_number=phone, username=user.username))
        message = "Successfully Updated Phone Number"

    response = twilio.twiml.Response()
    response.message(message)

    return str(response)
"""


def create_message(receiver, message):
    client.messages.create(to=receiver, from_=sender, body=message)


def send_message(user, post_array):
    message = None
    for post in post_array:
        if post["lang"] == user["language_pref"]:
            message = post["body"]
            create_message(user["phone_number"], message)
    if not message:
        print "ERROR, CAN'T FIND SPECIFIED LANGUAGE"


def send_message_to_all_users(post):
    for user in db.find_all(User):
        send_message(user, post.posts)
