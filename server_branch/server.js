const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');

const app = express();
const port = 6969;

app.use(cors());

// MongoDB connection
mongoose.connect('mongodb://localhost:27017/myntra')
  .then(() => {
    console.log('MongoDB connected');
  })
  .catch(err => {
    console.error('MongoDB connection error:', err);
    process.exit(1); // Exit process with failure
  });

// Define MongoDB schema and model
const priceSchema = new mongoose.Schema({
  priceInt: String,
  time: { type: Date },
  code: String,
  title: String,
});

const Price = mongoose.model('Price', priceSchema);

// API endpoint to fetch data based on product code
app.get('/chartData', async (req, res) => {
  try {
    const { productCode } = req.query;

    // Fetch data from MongoDB
    const data = await Price.find({ code: productCode }).sort({ time: 1 });

    // Send data as JSON response
    res.json(data);
  } catch (error) {
    console.error('Error fetching data:', error);
    res.status(500).json({ error: 'Internal server error' });
  }
});

// Start server
app.listen(port, () => {
  console.log(`Server is running on port ${port}`);
});
