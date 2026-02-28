import { BrowserRouter, Routes, Route } from 'react-router-dom'
import './App.css'

import Home from './Home.jsx'
import TODO from './TODO.jsx'
import SearchResult from './SearchResult.jsx'

function App() {

  return (
    <>

      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/todo/:id" element={<TODO />} />
          <Route path="/search/:searchedWord" element={<SearchResult />} />
        </Routes>
      </BrowserRouter>
    </>
  )
}

export default App
