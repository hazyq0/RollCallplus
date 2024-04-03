import requests
from bs4 import BeautifulSoup

url = "https://moeps.amucoe.ac.in/qr-code/verify/9a33d8fc-5467-4e6b-a53e-07ae70edfb48"

response = requests.get(url)
html_content = response.text

soup = BeautifulSoup(html_content, 'html.parser')
td_elements = soup.select('table.table td')

name = td_elements[1].get_text(strip=True)
print(name)


