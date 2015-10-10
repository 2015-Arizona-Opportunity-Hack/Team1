from twilio.rest import TwilioRestClient

account = "" #ADD ACCOUNT
token = "" #ADD TOKEN
sender = "" #ADD SENDER
client = TwilioRestClient(account, token)

def create_message(reciever, sender, message):
    send_message = client.messages.create(to=reciever, from_=sender, body=message)

def send_message(user, post_array):
    message = None
    for post in post_array:
        if post["lang"] == user["preferred_language"]:
            message = post["body"]
            create_message(user["username"], sender, message)
    if not message:
        print "ERROR, CAN'T FIND SPECIFIED LANGUAGE"
