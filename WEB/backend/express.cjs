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

// Generic placeholder GET endpoint
app.get('/api/placeholder', (req, res) => {
    const sql = `SELECT column_list FROM some_table WHERE id = ?;`; // skeleton only
    res.status(501).json({ message: 'TODO: implement /api/placeholder', sql, params: ['id'] });
});

// Generic placeholder POST endpoint
app.post('/api/placeholder', (req, res) => {
    const sql = `INSERT INTO some_table (col1, col2) VALUES (?, ?);`; // skeleton only
    res.status(501).json({ message: 'TODO: implement POST /api/placeholder', sql, params: ['col1', 'col2'] });
});

// Generic placeholder PATCH endpoint
app.patch('/api/placeholder', (req, res) => {
    const sql = `UPDATE some_table SET col1 = ? WHERE id = ?;`; // skeleton only
    res.status(501).json({ message: 'TODO: implement PATCH /api/placeholder', sql, params: ['col1', 'id'] });
});

// Generic placeholder DELETE endpoint
app.delete('/api/placeholder', (req, res) => {
    const sql = `DELETE FROM some_table WHERE id = ?;`; // skeleton only
    res.status(501).json({ message: 'TODO: implement DELETE /api/placeholder', sql, params: ['id'] });
});

app.use((req, res) => {
    res.status(404).json({ message: 'Not found' });
});

app.listen(port, () => {
    console.log(`API listening on http://localhost:${port}`);
});
