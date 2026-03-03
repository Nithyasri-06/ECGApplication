fetch("http://localhost:8080/patients")
    .then(response => {
        if (!response.ok) {
            throw new Error("Failed to fetch patients");
        }
        return response.json();
    })
    .then(data => {
        console.log("Patients data:", data); // DEBUG LINE

        const table = document.getElementById("patientTable");
        table.innerHTML = "";

        if (data.length === 0) {
            table.innerHTML = `
                <tr>
                    <td colspan="5" style="text-align:center;">No patients found</td>
                </tr>
            `;
            return;
        }

        data.forEach(p => {
            table.innerHTML += `
                <tr>
                    <td>${p.id}</td>
                    <td>${p.name}</td>
                    <td>${p.age}</td>
                    <td>${p.gender}</td>
                    <td>${formatDate(p.createdAt)}</td>
                </tr>
            `;
        });
    })
    .catch(error => {
        console.error("Error loading patients:", error);
    });

function formatDate(dateStr) {
    if (!dateStr) return "-";
    const d = new Date(dateStr);
    return d.toLocaleString();
}
function goHome() {
    window.location.href = "index.html";
}
