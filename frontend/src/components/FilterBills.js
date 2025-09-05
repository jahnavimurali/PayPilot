// import React, { useState } from "react";
// import axios from "axios";
// import {
//     PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer,
//     BarChart, Bar, XAxis, YAxis,
//     LineChart, Line,
//     AreaChart, Area
// } from "recharts";


// const FilterBills = () => {
//     const userId = JSON.parse(localStorage.getItem("user"))?.id || 0;
//     const [category, setCategory] = useState("");
//     const [bills, setBills] = useState([]);
//     const [loading, setLoading] = useState(false);

//     const handleSearch = async () => {
//         if (!category) return;
//         setLoading(true);
//         try {

//             const res = await axios.get(`http://localhost:9090/api/bills/${userId}/${category}`);
//             setBills(res.data);
//         } catch (err) {
//             console.error("Error fetching bills:", err);
//             alert("Error fetching bills.");
//         } finally {
//             setLoading(false);
//         }
//     };

//     // Calculate total amount for displayed bills
//     const total = bills.reduce((sum, bill) => sum + (bill.amount || 0), 0);

//     // Prepare chart data
//     // (If you want to group by month or something, let me know. Right now it just uses bills directly.)
//     const chartData = bills.map(bill => ({
//         title: bill.title,
//         amount: bill.amount,
//         dueDate: bill.dueDate ? bill.dueDate.substring(0, 10) : ""
//     }));


//     return (
//         <div className="mt-4">
//             <h3>Filter Bills by Category</h3>
//             <div className="input-group mb-3">
//                 <input
//                     type="text"
//                     placeholder="e.g. Rent, Groceries, Internet"
//                     className="form-control"
//                     value={category}
//                     onChange={(e) => {
//                         setCategory(e.target.value);
//                         setBills([]); // Reset results every time you edit the category
//                     }}
//                     autoFocus
//                 />
//                 <button
//                     className="btn btn-primary"
//                     onClick={handleSearch}
//                     disabled={!category}
//                 >
//                     Search
//                 </button>
//             </div>

//             {loading && <p>Loading...</p>}

//             {/* Show total for filtered results */}
//             {!loading && bills.length > 0 && (
//                 <div className="alert alert-info mb-2">
//                     Total for <b>{category}</b>: ₹{total}
//                 </div>
//             )}

//             {!loading && bills.length > 0 && (
//                 <>
//                     <table className="table table-bordered">
//                         <thead>
//                         <tr>
//                             <th>Title</th>
//                             <th>Category</th>
//                             <th>Amount</th>
//                             <th>Due Date</th>
//                         </tr>
//                         </thead>
//                         <tbody>
//                         {bills.map((bill) => (
//                             <tr key={bill.id}>
//                                 <td>{bill.title}</td>
//                                 <td>{bill.category}</td>
//                                 <td>₹{bill.amount}</td>
//                                 <td>{bill.dueDate ? bill.dueDate.substring(0, 10) : ""}</td>
//                             </tr>
//                         ))}
//                         </tbody>
//                     </table>

//                     {/* 1. Pie chart for split within this category */}
//                     <div className="my-4" style={{ width: "100%", minHeight: 300 }}>
//                         <h5>Pie Chart: Expense Split (Each Bill)</h5>
//                         <ResponsiveContainer width="100%" height={250}>
//                             <PieChart>
//                                 <Pie
//                                     data={chartData}
//                                     dataKey="amount"
//                                     nameKey="title"
//                                     cx="50%"
//                                     cy="50%"
//                                     outerRadius={80}
//                                     label
//                                 >
//                                     {chartData.map((entry, idx) => (
//                                         <Cell key={idx} fill={`hsl(${(idx * 70) % 360},70%,60%)`} />
//                                     ))}
//                                 </Pie>
//                                 <Tooltip />
//                                 <Legend />
//                             </PieChart>
//                         </ResponsiveContainer>
//                     </div>

//                     {/* 2. Bar chart */}
//                     <div className="my-4" style={{ width: "100%", minHeight: 300 }}>
//                         <h5>Bar Chart: Amount per Bill</h5>
//                         <ResponsiveContainer width="100%" height={250}>
//                             <BarChart data={chartData}>
//                                 <XAxis dataKey="title" />
//                                 <YAxis />
//                                 <Tooltip />
//                                 <Legend />
//                                 <Bar dataKey="amount" fill="#36a2eb" name="Amount" />
//                             </BarChart>
//                         </ResponsiveContainer>
//                     </div>

//                     {/* 3. Line chart */}
//                     <div className="my-4" style={{ width: "100%", minHeight: 300 }}>
//                         <h5>Line Chart: Amount per Bill</h5>
//                         <ResponsiveContainer width="100%" height={250}>
//                             <LineChart data={chartData}>
//                                 <XAxis dataKey="title" />
//                                 <YAxis />
//                                 <Tooltip />
//                                 <Legend />
//                                 <Line dataKey="amount" stroke="#ef476f" name="Amount" />
//                             </LineChart>
//                         </ResponsiveContainer>
//                     </div>

//                     {/* 4. Area chart */}
//                     <div className="my-4" style={{ width: "100%", minHeight: 300 }}>
//                         <h5>Area Chart: Amount per Bill</h5>
//                         <ResponsiveContainer width="100%" height={250}>
//                             <AreaChart data={chartData}>
//                                 <XAxis dataKey="title" />
//                                 <YAxis />
//                                 <Tooltip />
//                                 <Legend />
//                                 <Area dataKey="amount" stroke="#118ab2" fill="#90e0ef" name="Amount" />
//                             </AreaChart>
//                         </ResponsiveContainer>
//                     </div>
//                 </>
//             )}

//             {/* No results UX */}
//             {!loading && bills.length === 0 && category && (
//                 <p>No bills found for category "<b>{category}</b>"</p>
//             )}
//             {!loading && bills.length === 0 && !category && (
//                 <p>Type a category and search to see results.</p>
//             )}
//         </div>
//     );
// };


// export default FilterBills;


import React, { useState, useEffect } from "react";
import axios from "axios";
import {
  PieChart,
  Pie,
  Cell,
  Tooltip,
  Legend,
  ResponsiveContainer,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  LineChart,
  Line,
  AreaChart,
  Area,
} from "recharts";

const FilterBills = () => {
  const userId = JSON.parse(localStorage.getItem("user"))?.id || 0;

  // Get today's date in YYYY-MM-DD
  //const today = new Date().toISOString().split("T")[0];

  const [category, setCategory] = useState("");
  const [fromDate, setFromDate] = useState("");
  const [toDate, setToDate] = useState();
  const [allBills, setAllBills] = useState([]);
  const [filteredBills, setFilteredBills] = useState([]);
  const [loading, setLoading] = useState(true);

  // Fetch all bills once on mount
  useEffect(() => {
    const fetchBills = async () => {
      try {
        const res = await axios.get(
          `http://localhost:9090/api/bills/${userId}`
        );
        setAllBills(res.data);
        setFilteredBills(res.data); // initially show all
      } catch (err) {
        console.error("Error fetching bills:", err);
        alert("Error fetching bills.");
      } finally {
        setLoading(false);
      }
    };
    fetchBills();
  }, [userId]);

  // Filter bills whenever category/date changes
  useEffect(() => {
    let filtered = [...allBills];

    // Category filter
    if (category.trim()) {
      filtered = filtered.filter((bill) =>
        bill.category?.toLowerCase().includes(category.toLowerCase())
      );
    }

    // Date filters
    if (fromDate) {
      filtered = filtered.filter(
        (bill) => bill.dueDate && bill.dueDate >= fromDate
      );
    }
    if (toDate) {
      filtered = filtered.filter(
        (bill) => bill.dueDate && bill.dueDate <= toDate
      );
    }

    // Sort by dueDate (ascending)
    filtered.sort((a, b) =>
      a.dueDate && b.dueDate ? a.dueDate.localeCompare(b.dueDate) : 0
    );

    setFilteredBills(filtered);
  }, [category, fromDate, toDate, allBills]);

  // Calculate total amount for displayed bills
  const total = filteredBills.reduce(
    (sum, bill) => sum + (bill.amount || 0),
    0
  );

  // Prepare chart data
  const chartData = filteredBills.map((bill) => ({
    title: bill.title,
    amount: bill.amount,
    dueDate: bill.dueDate ? bill.dueDate.substring(0, 10) : "",
  }));

  return (
    <div className="mt-4">
      <h3>Filter Bills</h3>
      <div className="row mb-3">
        <div className="col-md-4">
          <input
            type="text"
            placeholder="e.g. Rent, Groceries, Internet"
            className="form-control"
            value={category}
            onChange={(e) => setCategory(e.target.value)}
          />
        </div>
        <div className="col-md-3">
          <input
            type="date"
            className="form-control"
            value={fromDate}
            onChange={(e) => setFromDate(e.target.value)}
          />
        </div>
        <div className="col-md-3">
          <input
            type="date"
            className="form-control"
            value={toDate}
            onChange={(e) => setToDate(e.target.value)}
          />
        </div>
      </div>

      {loading && <p>Loading...</p>}

      {!loading && filteredBills.length > 0 && (
        <div className="alert alert-info mb-2">
          Showing <b>{filteredBills.length}</b> bill(s){" "}
          {(category || fromDate || toDate) && (
            <>
              {category && (
                <>
                  {" "}
                  in category <b>{category}</b>
                </>
              )}
              {fromDate && (
                <>
                  {" "}
                  from <b>{fromDate}</b>
                </>
              )}
              {toDate && (
                <>
                  {" "}
                  to <b>{toDate}</b>
                </>
              )}
            </>
          )}
          <br />
          Total: ₹{total}
        </div>
      )}

      {/* Table + Charts */}
      {!loading && filteredBills.length > 0 && (
        <>
          <table className="table table-bordered">
            <thead>
              <tr>
                <th>Title</th>
                <th>Category</th>
                <th>Amount</th>
                <th>Due Date</th>
              </tr>
            </thead>
            <tbody>
              {filteredBills.map((bill) => (
                <tr key={bill.id}>
                  <td>{bill.title}</td>
                  <td>{bill.category}</td>
                  <td>₹{bill.amount}</td>
                  <td>{bill.dueDate ? bill.dueDate.substring(0, 10) : ""}</td>
                </tr>
              ))}
            </tbody>
          </table>

          {/* Pie chart */}
          <div className="my-4" style={{ width: "100%", minHeight: 300 }}>
            <h5>Pie Chart: Expense Split (Each Bill)</h5>
            <ResponsiveContainer width="100%" height={250}>
              <PieChart>
                <Pie
                  data={chartData}
                  dataKey="amount"
                  nameKey="title"
                  cx="50%"
                  cy="50%"
                  outerRadius={80}
                  label
                >
                  {chartData.map((entry, idx) => (
                    <Cell key={idx} fill={`hsl(${(idx * 70) % 360},70%,60%)`} />
                  ))}
                </Pie>
                <Tooltip />
                <Legend />
              </PieChart>
            </ResponsiveContainer>
          </div>

          {/* Bar chart */}
          <div className="my-4" style={{ width: "100%", minHeight: 300 }}>
            <h5>Bar Chart: Amount per Bill (by Date)</h5>
            <ResponsiveContainer width="100%" height={250}>
              <BarChart data={chartData}>
                <XAxis dataKey="dueDate" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Bar dataKey="amount" fill="#36a2eb" name="Amount" />
              </BarChart>
            </ResponsiveContainer>
          </div>

          {/* Line chart */}
          <div className="my-4" style={{ width: "100%", minHeight: 300 }}>
            <h5>Line Chart: Amount over Time</h5>
            <ResponsiveContainer width="100%" height={250}>
              <LineChart data={chartData}>
                <XAxis dataKey="dueDate" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Line dataKey="amount" stroke="#ef476f" name="Amount" />
              </LineChart>
            </ResponsiveContainer>
          </div>

          {/* Area chart */}
          <div className="my-4" style={{ width: "100%", minHeight: 300 }}>
            <h5>Area Chart: Amount over Time</h5>
            <ResponsiveContainer width="100%" height={250}>
              <AreaChart data={chartData}>
                <XAxis dataKey="dueDate" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Area
                  dataKey="amount"
                  stroke="#118ab2"
                  fill="#90e0ef"
                  name="Amount"
                />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </>
      )}

      {!loading && filteredBills.length === 0 && (
        <p>
          No bills found
          {(category || fromDate || toDate) && (
            <>
              {category && (
                <>
                  {" "}
                  for category "<b>{category}</b>"
                </>
              )}
              {fromDate && (
                <>
                  {" "}
                  from <b>{fromDate}</b>
                </>
              )}
              {toDate && (
                <>
                  {" "}
                  to <b>{toDate}</b>
                </>
              )}
            </>
          )}
        </p>
      )}
    </div>
  );
};

export default FilterBills;
