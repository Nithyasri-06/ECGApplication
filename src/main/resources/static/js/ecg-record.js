let ecgRecords = [];

function loadECG() {
    const patientId = document.getElementById("patientId").value;

    if (!patientId) {
        alert("Please enter Patient ID");
        return;
    }

    fetch(`http://localhost:8080/getRecords/${patientId}`)
        .then(res => {
            if (!res.ok) throw new Error("No records found");
            return res.json();
        })
        .then(data => {
            ecgRecords = data;

            const recordsBody = document.getElementById("recordsBody");
            recordsBody.innerHTML = "";

            if (!data || data.length === 0) {
                recordsBody.innerHTML =
                    `<tr><td colspan="4">No ECG records found</td></tr>`;
                return;
            }

            data.forEach((r, index) => {
                recordsBody.innerHTML += `
                    <tr>
                        <td>${index + 1}</td>
                        <td>${r.patientName}</td>
                        <td>${r.fileName}</td>
                        <td>${new Date(r.uploadedAt).toLocaleString()}</td>
                        <td>
                            <button class="view-btn" onclick="viewReport(${index})">
                                View Report
                            </button>
                        </td>
                    </tr>
                `;
            });
        })
        .catch(err => alert(err.message));
}


function viewReport(index) {
    const record = ecgRecords[index];

    if (!record || !record.id) {
        alert("Invalid ECG record");
        return;
    }

    // Redirect using URL param
    window.location.href = `result.html?id=${record.id}`;
}


function goHome() {
    window.location.href = "index.html";
}