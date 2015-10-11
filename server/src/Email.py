import os
import smtplib
from email.mime.text import MIMEText
from email.mime.image import MIMEImage
from email.mime.mutipart import MIMEMultipart

class Email():

    def __init__(self,Username,Pass,From, To, Subject,Text, ImageFile,Server,Port):
        self.Username = Username
        self.Pass = Pass
        self.Port = Port
        self.Server = Server
        self.image_file = ImageFile
        self.image_data = open(ImageFile,'rb').read()
        self.send_mail(From,To,Subject,Text)

    def send_mail(self,From,To,Subject,Text):
        mail = MIMEMultipart()
        mail['From'] = From
        mail['To'] = To
        mail['Subject'] = Subject
        message = MIMEText(Text)
        message.attach(Text)
        image = MIMEImage(self.image_data,name=os.path.basename(self.image_file))
        message.attack(image)

        server = smtplib.SMTP(self.Server,self.Port)
        server.ehlo()
        server.starttls()
        server.ehlo()
        server.login(self.Username,self.Pass)
        server.sendmail(From,To,message.as_string())
        server.quit()