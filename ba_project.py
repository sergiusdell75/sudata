#!/usr/bin/env python
# coding: utf-8

# ### Intelligenter Umzug

# In[1]:


import requests
import lxml.html as lh
import pandas as pd


# In[24]:


from bs4 import BeautifulSoup # library to parse HTML and XML documents


data = requests.get('https://www.geonames.org/postal-codes/DE/BE/berlin.html').text
soup = BeautifulSoup(data, 'html.parser')

for row in soup.find('table').find_all('tr'):
    cells = row.find_all('td')
    if(len(cells) > 0):
        print(cells[0].text)
#        postalCodeList.append(cells[0].text)
#        boroughList.append(cells[1].text)
#        neighborhoodList.append(cells[2].text.rstrip('\n'))


# In[25]:


soup


# In[20]:


# Create a handle, page, to handle the contents of the website
url='https://www.geonames.org/postal-codes/DE/BE/berlin.html'
page = requests.get(url)#Store the contents of the website under doc
doc = lh.fromstring(page.content)#Parse data that are stored between <tr>..</tr> of HTML
tr_elements = doc.xpath('//tr')#Create empty list
col=[]
i=0#For each row, store each first element (header) and an empty list
for t in tr_elements[0]:
    i+=1
    name=t.text_content()
    print(name)
#print(col)
for j in range(1,len(tr_elements)):
    T=tr_elements[j]
    print(len(T))


# In[21]:


# Create a handle, page, to handle the contents of the website
url='https://www.geonames.org/postal-codes/DE/BE/berlin.html'
page = requests.get(url)#Store the contents of the website under doc
doc = lh.fromstring(page.content)#Parse data that are stored between <tr>..</tr> of HTML
tr_elements = doc.xpath('//tr')#Create empty list
col=[]
i=0#For each row, store each first element (header) and an empty list
for t in tr_elements[0]:
    i+=1
    name=t.text_content()
    #print '%d:"%s"'%(i,name)
    col.append((name,[]))
for j in range(1,len(tr_elements)):
    T=tr_elements[j]
    if len(T)!=8:
        break
    i=0
    for t in T.iterchildren():
        data=t.text_content() 
        if i>0:
            try:
                data=int(data)
            except:
                pass
        col[i][1].append(data)
        i+=1
[len(C) for (title,C) in col]


# In[ ]:





# In[3]:


# check if the data are loaded
[len(C) for (title,C) in col]

