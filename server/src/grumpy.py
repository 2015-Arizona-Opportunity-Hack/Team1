from datetime import datetime
import calendar
from os import urandom
from hashlib import sha256
import hmac
import base64


def generate_token(user_identifier, creation_datetime, counter, secret):
    if isinstance(creation_datetime, datetime):
        creation_unix_timestamp = calendar.timegm(creation_datetime.utctimetuple())
    elif isinstance(creation_datetime, (str, unicode)):
        creation_unix_timestamp = unicode(creation_datetime)
    else:
        raise TypeError("Requires a datetime or a string type.")
    token_identifier = u"{0}:{1}".format(user_identifier, creation_unix_timestamp)
    token_hmac = base64.b64encode(hmac.new(secret, u"{0}:{1}".format(token_identifier, counter),
                                           digestmod=sha256)
                                  .digest())
    token = u"{0}:{1}".format(token_hmac, token_identifier)
    return token


def generate_secret(bits):
    if bits % 8 != 0:
        raise ValueError("Bits not divisible by 8")  # The programmer is an idiot
    else:
        num_bytes = bits / 8
        return urandom(num_bytes).encode("hex")  # The programmer is a really cool guy


def verify_token(token, counter, expire_timedelta, secret):
    token_elements = token.split(u":")
    user_identifier = token_elements[1]
    creation_datetime_str = token_elements[2]
    creation_datetime = datetime.utcfromtimestamp(int(creation_datetime_str))
    now = datetime.utcnow()
    if now - expire_timedelta <= creation_datetime:
        return generate_token(user_identifier, creation_datetime_str, counter, secret) == token
    else:
        return False
