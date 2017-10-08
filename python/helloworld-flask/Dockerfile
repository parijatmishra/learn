FROM python:2-alpine
MAINTAINER Parijat Mishra
COPY . /app
WORKDIR /app
RUN pip install --no-cache-dir -r requirements.txt
ENV PORT 8000
EXPOSE 8000
CMD ["python", "application.py"]

