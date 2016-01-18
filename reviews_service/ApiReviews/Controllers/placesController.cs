using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity;
using System.Data.Entity.Infrastructure;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using System.Web.Http.Description;
using ApiReviews.Models;
using System.Web.Http.Cors;

namespace ApiReviews.Controllers
{
    [EnableCors(origins: "*", headers: "*", methods: "*")]
    public class placesController : ApiController
    {
        private ReviewsModel db = new ReviewsModel();

        // GET: api/places
        public IQueryable<place> Getplaces()
        {
            return db.places;
        }

        // GET: api/places/5
        [ResponseType(typeof(place))]
        public IHttpActionResult Getplace(int id)
        {
            place place = db.places.Find(id);
            if (place == null)
            {
                return NotFound();
            }

            return Ok(place);
        }



        // PUT: api/places/5
        [ResponseType(typeof(void))]
        public IHttpActionResult Putplace(int id, place place)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            if (id != place.Id)
            {
                return BadRequest();
            }

            db.Entry(place).State = EntityState.Modified;

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!placeExists(id))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return StatusCode(HttpStatusCode.NoContent);
        }

        // POST: api/places
        [ResponseType(typeof(place))]
        public IHttpActionResult Postplace(place place)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            db.places.Add(place);
            db.SaveChanges();

            return CreatedAtRoute("DefaultApi", new { id = place.Id }, place);
        }

        // DELETE: api/places/5
        [ResponseType(typeof(place))]
        public IHttpActionResult Deleteplace(int id)
        {
            place place = db.places.Find(id);
            if (place == null)
            {
                return NotFound();
            }

            db.places.Remove(place);
            db.SaveChanges();

            return Ok(place);
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }

        private bool placeExists(int id)
        {
            return db.places.Count(e => e.Id == id) > 0;
        }
    }
}