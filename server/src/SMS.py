from twilio.rest import TwilioRestClient

account = "" #ADD ACCOUNT
token = "" #ADD TOKEN
client = TwilioRestClient(account, token)

def createMessage(reciever, sender, message):
    send_message = client.messages.create(to=reciever,from_=sender,body = message)