document.addEventListener("DOMContentLoaded", function () {

     const params = new URLSearchParams(window.location.search);
     const recordId = params.get("id");

     if (recordId) {

         // ============ VIEW RECORD FLOW ============

         fetch(`http://localhost:8080/getRecordById/${recordId}`)
             .then(res => {
                 if (!res.ok) throw new Error("Record not found");
                 return res.json();
             })
             .then(data => {

                 if (!data || !data.analysis || !data.signal) {
                     alert("No valid ECG data found");
                     window.location.href = "ecg-records.html";
                     return;
                 }

                 displayResult(data);
             })
             .catch(err => {
                 console.error(err);
                 alert("Server error while loading ECG report");
                 window.location.href = "ecg-records.html";
             });

     } else {

         // ============ UPLOAD ECG FLOW ============

         let result = localStorage.getItem("ecgResult");

         if (!result) {
             alert("No ECG result found");
             window.location.href = "ecg-upload.html";
             return;
         }

         result = JSON.parse(result);

         if (!result || !result.signal || !result.analysis) {
             alert("No valid ECG data found");
             window.location.href = "ecg-upload.html";
             return;
         }

         displayResult(result);
     }

 });

// ==================== DISPLAY ECG REPORT ====================

function displayResult(result) {

    const analysis = result.analysis;

    document.getElementById("heartRate").innerText =
        analysis.signal_analysis?.heart_rate || "N/A";

    document.getElementById("condition").innerText =
        analysis.signal_analysis?.rhythm_category || "N/A";

    document.getElementById("risk").innerText =
        analysis.clinical_interpretation?.risk_level || "N/A";

    document.getElementById("status").innerText =
        analysis.clinical_interpretation?.cardiac_status || "N/A";

    const morph = analysis.morphological_analysis || {};
    let beatTypeText = morph.overall_beat_type || "Normal";

    if (morph.abnormal_beat_distribution &&
        Object.keys(morph.abnormal_beat_distribution).length > 0) {

        const abn = Object.entries(morph.abnormal_beat_distribution)
            .map(([type, count]) => `${type}: ${count}`)
            .join(", ");

        beatTypeText += ` (${abn})`;
    }

    document.getElementById("beatType").innerText = beatTypeText;

    document.getElementById("confidence").innerText =
        morph.model_confidence || "N/A";

    document.getElementById("recommendation").innerText =
        analysis.clinical_interpretation?.recommendation || "N/A";

    // ================= ECG CHART =================

    const rawSignal = result.signal;
    const fs = result.samplingRate || 360;

    const displaySeconds = 2;
    const maxSamples = fs * displaySeconds;

    const displaySignal = rawSignal.slice(0, maxSamples);

    const ecgSignal = displaySignal.map((val, idx) => ({
        x: idx / fs,
        y: val
    }));

    const ctx = document.getElementById("ecgChart").getContext("2d");

    new Chart(ctx, {
        type: "line",
        data: {
            datasets: [{
                label: "ECG Signal",
                data: ecgSignal,
                borderWidth: 1.5,
                pointRadius: 0,
                tension: 0.25
            }]
        },
        options: {
            responsive: true,
            animation: false,
            parsing: false,
            scales: {
                x: {
                    type: "linear",
                    title: { display: true, text: "Time (seconds)" },
                    min: 0,
                    max: displaySeconds
                },
                y: {
                    title: { display: true, text: "Amplitude (mV)" }
                }
            }
        }
    });
}
function goHome() {
    window.location.href = "index.html";
}