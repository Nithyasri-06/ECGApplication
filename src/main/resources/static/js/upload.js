async function uploadECG() {
    const patientId = document.getElementById("patientId").value;
    const fileInput = document.getElementById("ecgFile");

    if (!patientId || fileInput.files.length === 0) {
        alert("Please enter Patient ID and select ECG CSV file");
        return;
    }

    const file = fileInput.files[0];
    const formData = new FormData();
    formData.append("file", file);

    try {
        const response = await fetch(`http://localhost:8080/upload/${patientId}`, {
            method: "POST",
            body: formData
        });

        if (!response.ok) {
            const errText = await response.text();
            throw new Error(`Upload failed: ${response.status} ${errText}`);
        }

        const data = await response.json();

        // Check if signal exists
        if (!data.signal || data.signal.length === 0) {
            alert("No valid ECG signal received from backend");
            return;
        }

        // ==================== LIMIT SIGNAL FOR LOCALSTORAGE ====================
        const fs = data.samplingRate || 360;               // sampling rate
        const limitedSignal = data.signal.slice(0, fs * 2); // first 2 seconds only

        const storeData = {
            analysis: data.analysis,
            signal: limitedSignal,           // small slice for chart
            samplingRate: data.samplingRate,
            fileName: data.fileName,
            uploadedAt: data.uploadedAt
        };

        // Save to localStorage (won’t exceed quota)
        localStorage.setItem("ecgResult", JSON.stringify(storeData));
        console.log("Stored ECG Analysis:", localStorage.getItem("ecgResult"));

        // Redirect to result page
        window.location.href = "result.html";

    } catch (error) {

        console.error(error);
        alert("ECG upload failed. Check console for details.");
    }
}