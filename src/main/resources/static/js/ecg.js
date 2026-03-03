document.getElementById("ecgForm")?.addEventListener("submit", e => {
    e.preventDefault();

    const formData = new FormData();
    formData.append("patientId", patientId.value);
    formData.append("file", file.files[0]);

    fetch("http://localhost:8080/api/ecg/upload/{id}", {
        method: "POST",
        body: formData
    })
    .then(res => res.json())
    .then(() => msg.innerText = "ECG uploaded successfully");
});
