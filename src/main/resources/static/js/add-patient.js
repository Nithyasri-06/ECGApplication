document.getElementById("patientForm").addEventListener("submit", function (e) {
    e.preventDefault();

    const name = document.getElementById("name").value;
    const age = document.getElementById("age").value;
    const gender = document.getElementById("gender").value;

    const patient = {
        name: name,
        age: age,
        gender: gender
    };

    fetch("http://localhost:8080/patients/add", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(patient)
    })
    .then(res => res.json())
    .then(data => {
        document.getElementById("msg").innerHTML = `
            ✅ Patient Added Successfully! <br><br>
            <strong>Name:</strong> ${data.name}<br>
            <strong>Age:</strong> ${data.age}<br>
            <strong>Gender:</strong> ${data.gender}
        `;

        document.getElementById("patientForm").reset();
    })
    .catch(err => {
        document.getElementById("msg").innerText = "❌ Error adding patient";
    });
});

function goHome() {
    window.location.href = "index.html";
}
