import React, { useEffect, useState } from "react";
import axios from "axios";

function App() {
  const [files, setFiles] = useState([]);
  const [selectedFile, setSelectedFile] = useState(null);

  const API_URL = "http://localhost:8080/api/files";

  // Fetch all files from backend
  const fetchFiles = async () => {
    try {
      const response = await axios.get(`${API_URL}/all`);
      setFiles(response.data);
    } catch (error) {
      console.error("Error fetching files:", error);
    }
  };

  useEffect(() => {
    fetchFiles();
  }, []);

  // Upload file
  const handleUpload = async () => {
    if (!selectedFile) return alert("Please select a file");

    const formData = new FormData();
    formData.append("file", selectedFile);

    try {
      await axios.post(`${API_URL}/upload`, formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });
      setSelectedFile(null);
      fetchFiles(); // Refresh list
      alert("File uploaded successfully!");
    } catch (error) {
      console.error("Upload error:", error);
      alert("Failed to upload file");
    }
  };

  // Download file
  const handleDownload = async (file) => {
    try {
      const response = await axios.get(`${API_URL}/download/${file.id}`, {
        responseType: "blob", // Important for file download
      });

      // Create a download link
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", file.fileName); // Original filename
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (error) {
      console.error("Download error:", error);
      alert("Failed to download file");
    }
  };

  return (
    <div style={{ maxWidth: "600px", margin: "50px auto", textAlign: "center" }}>
      <h2>My Disk Space</h2>

      {/* Upload Section */}
      <div style={{ marginBottom: "20px" }}>
        <input
          type="file"
          onChange={(e) => setSelectedFile(e.target.files[0])}
        />
        <button onClick={handleUpload} style={{ marginLeft: "10px" }}>
          Upload
        </button>
      </div>

      {/* Files List */}
      <table width="100%" border="1" cellPadding="10" style={{ borderCollapse: "collapse" }}>
        <thead>
          <tr>
            <th>File Name</th>
            <th>Size (bytes)</th>
            <th>Download</th>
          </tr>
        </thead>
        <tbody>
          {files.length === 0 && (
            <tr>
              <td colSpan="3">No files uploaded yet</td>
            </tr>
          )}
          {files.map((file) => (
            <tr key={file.id}>
              <td>{file.fileName.substring(37)}</td>
              <td>{file.fileSize}</td>
              <td>
                <button onClick={() => handleDownload(file)}>Download</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default App;
