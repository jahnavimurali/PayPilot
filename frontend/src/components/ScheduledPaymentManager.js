import React, { useState, useEffect } from "react";
import axios from "axios";

const ScheduledPaymentManager = () => {
    const user = JSON.parse(localStorage.getItem("user"));
    const [bills, setBills] = useState([]);
    const [scheduledPayments, setScheduledPayments] = useState([]);
    const [formData, setFormData] = useState({
        id: null,
        billId: "",
        amount: "",
        paymentMethod: "",
        scheduledDate: new Date().toISOString().split("T")[0], // default = today
    });
    const [viewType, setViewType] = useState("all");

    useEffect(() => {
        fetchBills();
    }, []);

    useEffect(() => {
        fetchScheduledPayments();
    }, [viewType]);

    const fetchBills = async () => {
        try {
            const res = await axios.get("http://localhost:9090/api/bills");
            setBills(res.data.filter((b) => b.userId === user.id));
        } catch (err) {
            console.error("Error fetching bills:", err);
        }
    };

    const fetchScheduledPayments = async () => {
        try {
            let res;
            if (viewType === "all") {
                res = await axios.get(
                    `http://localhost:9090/api/scheduled-payments/user/${user.id}`
                );
            } else if (viewType === "upcoming") {
                res = await axios.get(
                    "http://localhost:9090/api/scheduled-payments/upcoming"
                );
            } else {
                res = await axios.get(
                    "http://localhost:9090/api/scheduled-payments/history"
                );
            }
            const data =
                viewType === "all"
                    ? res.data
                    : res.data.filter((sp) => sp.userId === user.id);
            setScheduledPayments(data);
        } catch (err) {
            console.error("Error fetching scheduled payments:", err);
        }
    };

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            if (formData.id) {
                // Update existing payment
                await axios.put(
                    `http://localhost:9090/api/scheduled-payments/${formData.id}`,
                    {
                        ...formData,
                        userId: user.id,
                    }
                );
            } else {
                // Create new payment
                await axios.post("http://localhost:9090/api/scheduled-payments", {
                    ...formData,
                    userId: user.id,
                });
            }

            fetchScheduledPayments();
            resetForm();
        } catch (err) {
            console.error("Error saving payment:", err);
        }
    };

    const handleEdit = (payment) => {
        setFormData({
            id: payment.id,
            billId: payment.billId,
            amount: payment.amount,
            paymentMethod: payment.paymentMethod,
            scheduledDate: payment.scheduledDate,
        });
    };

    const handleDelete = async (id) => {
        if (!window.confirm("Are you sure you want to delete this payment?"))
            return;
        try {
            await axios.delete(`http://localhost:9090/api/scheduled-payments/${id}`);
            fetchScheduledPayments();
        } catch (err) {
            console.error("Error deleting payment:", err);
        }
    };

    const resetForm = () => {
        setFormData({
            id: null,
            billId: "",
            amount: "",
            paymentMethod: "",
            scheduledDate: new Date().toISOString().split("T")[0],
        });
    };

    return (
        <div className="container mt-4">
            <h3>💳 {formData.id ? "Edit Payment" : "Schedule a Payment"}</h3>

            <form className="row g-3 align-items-end" onSubmit={handleSubmit}>
                <div className="col-md-3">
                    <label className="form-label">Bill</label>
                    <select
                        className="form-select"
                        name="billId"
                        value={formData.billId}
                        onChange={handleChange}
                        required
                    >
                        <option value="">-- select bill --</option>
                        {bills.map((b) => (
                            <option key={b.id} value={b.id}>
                                {b.title} (₹{b.amount})
                            </option>
                        ))}
                    </select>
                </div>

                <div className="col-md-2">
                    <label className="form-label">Amount</label>
                    <input
                        type="number"
                        name="amount"
                        className="form-control"
                        value={formData.amount}
                        onChange={handleChange}
                        required
                    />
                </div>

                <div className="col-md-3">
                    <label className="form-label">Payment Method</label>
                    <input
                        type="text"
                        name="paymentMethod"
                        className="form-control"
                        value={formData.paymentMethod}
                        onChange={handleChange}
                        required
                    />
                </div>

                <div className="col-md-2">
                    <label className="form-label">Date</label>
                    <input
                        type="date"
                        name="scheduledDate"
                        className="form-control"
                        value={formData.scheduledDate}
                        onChange={handleChange}
                        required
                    />
                </div>

                <div className="col-md-2 d-flex">
                    <button type="submit" className="btn btn-primary w-100 me-2">
                        {formData.id ? "Update" : "Schedule"}
                    </button>
                    {formData.id && (
                        <button
                            type="button"
                            className="btn btn-secondary w-100"
                            onClick={resetForm}
                        >
                            Cancel
                        </button>
                    )}
                </div>
            </form>

            <hr className="my-4" />

            <div className="btn-group mb-3">
                <button
                    className={`btn ${
                        viewType === "all" ? "btn-secondary" : "btn-outline-secondary"
                    }`}
                    onClick={() => setViewType("all")}
                >
                    All
                </button>
                <button
                    className={`btn ${
                        viewType === "upcoming" ? "btn-info" : "btn-outline-info"
                    }`}
                    onClick={() => setViewType("upcoming")}
                >
                    Upcoming
                </button>
                <button
                    className={`btn ${
                        viewType === "history" ? "btn-warning" : "btn-outline-warning"
                    }`}
                    onClick={() => setViewType("history")}
                >
                    History
                </button>
            </div>

            <table className="table table-bordered">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Bill</th>
                    <th>Amount</th>
                    <th>Method</th>
                    <th>Date</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {scheduledPayments.map((sp) => (
                    <tr key={sp.id}>
                        <td>{sp.id}</td>
                        <td>
                            {bills.find((b) => b.id === sp.billId)?.title || sp.billId}
                        </td>
                        <td>₹{sp.amount}</td>
                        <td>{sp.paymentMethod}</td>
                        <td>{sp.scheduledDate}</td>
                        <td>
                            <button
                                className="btn btn-sm btn-info me-2"
                                onClick={() => handleEdit(sp)}
                            >
                                Edit
                            </button>
                            <button
                                className="btn btn-sm btn-danger"
                                onClick={() => handleDelete(sp.id)}
                            >
                                Delete
                            </button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

export default ScheduledPaymentManager;

