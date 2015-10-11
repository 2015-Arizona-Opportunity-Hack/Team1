import smtplib
from __init__ import db
from db_layer import User


def send_message_to_all_users(post):
    for user in db.find_all(User):
        smtp_obj = smtplib.SMTP("localhost")
        for user in db.find_all(User):
            if not user["email"]:
                continue
            send_message(post.posts, user, smtp_obj)


def send_message(posts, user, smtp_obj):
    message = None
    for post in posts:
        if post["lang"] == user["language_pref"]:
                message = post["body"]
                smtp_obj.sendmail("admin@gideon.stevex86@gmail.com", [user["email"]], message)
        if not message:
            print "ERROR, CAN'T FIND SPECIFIED LANGUAGE"
