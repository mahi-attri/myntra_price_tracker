from selenium import webdriver 
from selenium.webdriver.chrome.options import Options
from notifypy import Notify
from bs4 import BeautifulSoup
import os
from datetime import datetime


def notify():
    notification= Notify()
    notification.title="Extracting Data"
    notification.message= "Extracting data from Myntra"
    notification.send()

def get_data():
    options= Options()
    #options.add_argument("--headless")
    # #user_agnt=


    with open("products.txt") as f:
        products= f.readlines()
    
    driver=webdriver.Chrome(options=options)

    i=0
    for product in products:
        driver.get(product)
        i+=1
        page_source=driver.page_source
        with open(f"data/{i}.html","w",encoding="utf-8") as f:
            f.write(page_source)
    
def extract_data():
    files=os.listdir("data")
    for file in files:
        print(file)
        with open(f"data/{file}",encoding="utf-8") as f:
            content=f.read()
        
        soup= BeautifulSoup(content, 'html.parser')
        title= soup.title.getText().split("|")[0].split("-")[0]
        time=datetime.now()

        price= soup.find(class_="pdp-price")
        priceInt = price.getText().strip() if price else "Price not found"
        code= soup.find(class_="supplier-styleId")
        code = code.getText().strip() if code else "Code not found"
        print(priceInt,code,title,time)
        with open("finaldata.txt","a",encoding="utf-8") as f:
            f.write(f"{priceInt}~~{code}~~{title}~~{time}\n")
    
if __name__=="__main__":
    notify()

    get_data()
    extract_data()