const express = require('express');
const app = express();
const port = process.env.PORT || 3000;
const version = process.env.VERSION || '1.0.0';
const environment = process.env.ENVIRONMENT || 'unknown';

app.get('/', (req, res) => {
    res.json({
        message: 'Hello from Blue-Green Deployment Demo!',
        version: version,
        environment: environment,
        timestamp: new Date().toISOString(),
        hostname: require('os').hostname(),
        color: environment === 'blue' ? '#007bff' : '#28a745'
    });
});

app.get('/health', (req, res) => {
    res.status(200).json({
        status: 'healthy',
        environment: environment,
        version: version,
        uptime: process.uptime()
    });
});

app.get('/version', (req, res) => {
    res.json({
        version: version,
        environment: environment
    });
});

app.listen(port, () => {
    console.log(`[${environment.toUpperCase()}] Server running on port ${port}`);
    console.log(`Version: ${version}`);
    console.log(`Environment: ${environment}`);
});