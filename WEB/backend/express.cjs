const express = require('express');
const cors = require('cors');
const mysql = require('mysql2');

const app = express();
const port = 3333;

const connection = mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: '',
    database: 'CHANGEME_DB_NAME',
});

app.use(cors());
app.use(express.json());

app.get('/api/', (req, res) => {
    const sql = `

    `;
    connection.query(sql, (err, results) => {
        if (err) {
            console.error('Database error:', err);
            return res.status(500).json({ message: 'Internal server error' });
        }
        res.status(200).json(results);
    });

});


app.post('/api/', (req, res) => {
    const sql = `

    `;
    connection.query(sql, (err, results) => {
        if (err) {
            console.error('Database error:', err);
            return res.status(500).json({ message: 'Internal server error' });
        }
        res.status(200).json(results);
    });
});

app.patch('/api/', (req, res) => {
    const sql = `

    `;
    connection.query(sql, (err, results) => {
        if (err) {
            console.error('Database error:', err);
            return res.status(500).json({ message: 'Internal server error' });
        }
        res.status(200).json(results);
    });
});

app.delete('/api/', (req, res) => {
    const sql = `

    `;
    connection.query(sql, (err, results) => {
        if (err) {
            console.error('Database error:', err);
            return res.status(500).json({ message: 'Internal server error' });
        }
        res.status(200).json(results);
    });
});

app.use((req, res) => {
    res.status(404).json({ message: 'Not found' });
});

app.listen(port, () => {
    console.log(`API listening on http://localhost:${port}`);
});
