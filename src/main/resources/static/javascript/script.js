async function loadDetails() {
    try {
        const response = await fetch('/json/details.json');
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        const details = await response.json();
        document.getElementById('project-title').textContent = details.title;
        document.getElementById('project-description').textContent = details.description;

        const featuresList = document.getElementById('project-features');
        featuresList.innerHTML = '';
        details.features.forEach(feature => {
            const li = document.createElement('li');
            li.textContent = feature;
            featuresList.appendChild(li);
        });
    } catch (error) {
        console.error('Error loading project details:', error);
    }
}

async function loadEndpoints() {
    try {
        const response = await fetch('/json/endpoints.json');
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        const endpoints = await response.json();
        const endpointsList = document.getElementById('endpoints-list');
        endpointsList.innerHTML = '';
        endpoints.forEach(endpoint => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${endpoint.method}</td>
                <td>${endpoint.path}</td>
                <td>${endpoint.description}</td>
            `;
            endpointsList.appendChild(row);
        });
    } catch (error) {
        console.error('Error loading endpoints:', error);
    }
}

document.addEventListener('DOMContentLoaded', async () => {
    await loadDetails();
    await loadEndpoints();
});
