import React, { useState } from "react";
import axios from "axios";

const AddBill = () => {
    const [billData, setBillData] = useState({
        title: "",
        category: "",
        amount: "",
        dueDate: "",
        userId: JSON.parse(localStorage.getItem("user"))?.id || 0,
    });

    const [successMessage, setSuccessMessage] = useState("");

    const handleChange = (e) => {
        setBillData({ ...billData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await axios.post("http://localhost:9090/api/bills", billData);
            setSuccessMessage("✅ Bill added successfully!");
            setBillData({ ...billData, title: "", category: "", amount: "", dueDate: "" });

            // hide message after 3 seconds
            setTimeout(() => setSuccessMessage(""), 3000);
        } catch (err) {
            console.error("Error adding bill:", err);
            setSuccessMessage("❌ Failed to add bill. Try again.");
        }
    };

    return (
        <div>
            <h3>Add a New Bill</h3>

            {successMessage && (
                <div className="alert alert-info mt-3" role="alert">
                    {successMessage}
                </div>
            )}

            <form className="mt-4" onSubmit={handleSubmit}>
                <div className="mb-3">
                    <label>Title</label>
                    <input type="text" className="form-control" name="title" value={billData.title} onChange={handleChange} required />
                </div>
                <div className="mb-3">
                    <label>Category</label>
                    <input type="text" className="form-control" name="category" value={billData.category} onChange={handleChange} required />
                </div>
                <div className="mb-3">
                    <label>Amount (₹)</label>
                    <input type="number" className="form-control" name="amount" value={billData.amount} onChange={handleChange} required />
                </div>
                <div className="mb-3">
                    <label>Due Date</label>
                    <input type="date" className="form-control" name="dueDate" value={billData.dueDate} onChange={handleChange} required />
                </div>
                <button type="submit" className="btn btn-success">➕ Add Bill</button>
            </form>
        </div>
    );
};

export default AddBill;