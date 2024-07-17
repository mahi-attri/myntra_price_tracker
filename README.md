Myntra Price Tracker involves these tasks:
 
 • Data Extraction Process:

The ‘main.py’ script automates the extraction of product pricing data from Myntra, leveraging Selenium for web automation, BeautifulSoup for HTML parsing, MongoDB for data storage, and notifypy for notifications. The workflow begins with setting up a connection to a local MongoDB instance, where the ‘prices’ collection is selected. A desktop notification is sent to indicate the start of the data extraction process.

Workflow-

The data fetching process involves reading product URLs from ‘products.txt’ and using Selenium to fetch and save the HTML content of each product page. During data extraction, the saved HTML files are parsed with BeautifulSoup to extract the product title, price, and code. This extracted data is then inserted into MongoDB and appended to ‘finaldata.txt’. The script executes the ‘notify()’, ‘get_data()’, and ‘extract_data()’ functions sequentially to complete the process.



 • Server Branch:

This project includes a Node.js server that connects to a MongoDB database and provides an API to fetch product pricing data. The server is set up using Express for handling HTTP requests, Mongoose for MongoDB object modeling, and CORS for enabling cross-origin requests. Upon initialization, the server connects to a local MongoDB instance, logs connection status, and defines a schema and model for storing product pricing information, including fields for price, time, product code, and title.

An API endpoint ‘/chartData’ is provided to fetch product pricing data based on a product code. The server queries the MongoDB collection for matching records, sorts the results by time, and returns the data as a JSON response. To use the server, ensure MongoDB is running locally with the relevant data, start the Node.js application, and access the endpoint to retrieve pricing data for specified product codes.



 • Android App Layout and Functionality:

The Android application is structured using a ‘RelativeLayout’, which includes sections for a top bar, input, and data display. The top bar contains a ‘TextView’ that displays the application title, styled with specific text size, color, and padding. Below the top bar, an input section contains an ‘EditText’ for entering product codes and a ‘Button’ for submitting the input. The data display section is a ‘ScrollView’ that houses a ‘TextView’ for showing the fetched product data.

Application Logic-

The main functionality is implemented in ‘MainActivity’. It initializes views such as the product code input field, data display text view, and submit button. An ‘OkHttpClient’ instance is used for making network requests to fetch product data. When the submit button is clicked, the app retrieves the entered product code and sends a request to a specified server endpoint. The server response, containing product data, is parsed and displayed in the data text view. The app groups and sorts the fetched data by product name and displays it in a user-friendly format.
