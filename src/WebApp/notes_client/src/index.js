import React, { useEffect, useState } from "react"
import ReactDOM from 'react-dom'
import './styles/main.css'
import { Note } from "./components/note" 
import { MarkdownDisplay } from "./components/markdownDisplay"

const URL = 'http://127.0.0.1:8000/'

const App = () =>{

    const [modelVisible,setModelVisible]=useState(false);
    const [title,setTitle] = useState('')
    const [content,setContent] = useState('')
    const [notes,setNotes] = useState([])
    const [selectedMarkdown, setSelectedMarkdown] = useState('');

    const createNote = async (event) =>{
        event.preventDefault();

        const new_request = new Request(
            `${URL}/notes/`,
            {
                body:JSON.stringify({title,content}),
                headers:{
                    'Content-Type':'application/json'
                },
                
                method:'POST'
            }
        );

        const response = await fetch(new_request)
        const data = await response.json();
        if(response.ok) {
            console.log(data)
        }
        else {
            console.log('Failed network request')
        }
    
        setTitle('')
        setContent('')

        setModelVisible(false)
        getAllNotes()
    }

    const getAllNotes = async () => {
        const response = await fetch(`${URL}/notes/`)
        const data = await response.json()

        if(response.ok) {
            //console.log(data)
            setNotes(data)
        }
        else {
            console.log('failed network request')
        }
    }

    const deleteItem = async (noteID) => {
        console.log(noteID)

        const response = await fetch(`${URL}/notes/${noteID}/`, {
            method:'DELETE'
        })

        if(response.ok) {
            console.log(noteID + " success")
            getAllNotes()
        }
        else {
            console.log('failed network request')
        }
    }

    const displayMarkdown = (filename) => {
        setSelectedMarkdown(filename);
    }

    useEffect(
        () => {
            getAllNotes()
        },[]
    )

    return (
        <div>
            <div className='header'>
                <div className='logo'>
                    <p className='title'>Notes App</p>
                </div>
                <div className='add-note'>
                    <a className='add-button' href='#' onClick={()=>setModelVisible(true)}>Add Note</a>
                </div>
            </div>
            {notes.length > 0? 
                (<div className="notes-list">
            {
                    notes.map(
                        (item) => (
                            <div key={item.id}>
                            <Note
                                title={item.title}
                                content={item.content}
                                onClick={() => deleteItem(item.id)}
                            />
                            <button onClick={() => displayMarkdown(`${item.title}.md`)}>
                                View Markdown
                            </button>
                        </div>
                        )
                    )
            }
                </div>)
                :(
                    <div className='notes'>
                        <p className='centerText'>No Notes</p>
                    </div>
                )
            }

            {selectedMarkdown && (
                <MarkdownDisplay filename={selectedMarkdown} />
            )}
            <div className={modelVisible? 'model':'model-invisible'}>
                <div className='form'>
                    <div className='form-header'>
                        <div>
                            <p className='form-header-text'>Create A Note</p>
                        </div>
                        <div>
                            <a href='#' className='close-button' onClick={()=>setModelVisible(false)}>X</a>
                        </div>
                    </div>
                    <form action=''>
                        <div className='form-group'>
                            <label htmlFor='title'>Title</label>
                            <input type='text' name='title' id='title' value={title} onChange={(e)=>setTitle(e.target.value)} className='form-control' required/>
                        </div>
                        <div className='form-group'>
                            <label htmlFor='content'>Content</label>
                            <textarea name='content' id='' cols='30' value={content} onChange={(e)=>setContent(e.target.value)} rows='5' className='form-control' required/>
                        </div>
                        <div className='form-group'>
                            <input type='submit' value='Save' className='button' onClick={createNote}/>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    )
}

ReactDOM.render(<App/>, document.querySelector('#root'));