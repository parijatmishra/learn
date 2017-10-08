from flask.ext.wtf import Form
from wtforms import PasswordField, BooleanField, SubmitField, StringField
from wtforms.fields.html5 import EmailField
from wtforms.validators import Required, Length, Email

class LoginForm(Form):
    email = EmailField('Email', validators=[Required(), Length(1, 128)])
    password = PasswordField('Password', validators=[Required()])
    remember_me = BooleanField('Keep me logged in')
    submit = SubmitField('Log In')

class RegistrationForm(Form):
    email = EmailField('Email', validators=[Required(), Length(1, 128)])
    username = StringField('Username', validators=[Required(), Length(1, 64)])
    password = PasswordField('Password', validators=[Required()])
    password2 = PasswordField('Confirm Password', validators=[Required()])
    submit = SubmitField('Register')
